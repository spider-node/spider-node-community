/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.spider.framework.transaction.sdk.datasource;

import cn.spider.framework.transaction.sdk.core.model.BranchStatus;
import cn.spider.framework.transaction.sdk.datasource.isolate.IsolateManager;
import cn.spider.framework.transaction.sdk.datasource.isolate.TransactionOperationStatus;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogManager;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogManagerFactory;
import cn.spider.framework.transaction.sdk.thread.NamedThreadFactory;
import cn.spider.framework.transaction.sdk.util.CollectionUtils;
import cn.spider.framework.transaction.sdk.util.IOUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * The type Async worker.
 *
 * @author DDS
 */
public class AsyncWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncWorker.class);

    private static final int DEFAULT_RESOURCE_SIZE = 16;

    private static final int UNDOLOG_DELETE_LIMIT_SIZE = 1000;

    private static final int ASYNC_COMMIT_BUFFER_LIMIT = 50;

    private final DataSourceManager dataSourceManager;

    private final BlockingQueue<Phase2Context> commitQueue;

    private final ScheduledExecutorService scheduledExecutor;

    private final IsolateManager isolateManager;

    public AsyncWorker(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;

        LOGGER.info("Async Commit Buffer Limit: {}", ASYNC_COMMIT_BUFFER_LIMIT);
        commitQueue = new LinkedBlockingQueue<>(ASYNC_COMMIT_BUFFER_LIMIT);

        ThreadFactory threadFactory = new NamedThreadFactory("AsyncWorker", 2, true);
        scheduledExecutor = new ScheduledThreadPoolExecutor(2, threadFactory);
        scheduledExecutor.scheduleAtFixedRate(this::doBranchCommitSafely, 10, 1000, TimeUnit.MILLISECONDS);
        isolateManager = IsolateManager.getIsolateManager();
    }

    public BranchStatus branchCommit(String xid, long branchId, String resourceId) {
        Phase2Context context = new Phase2Context(xid, branchId, resourceId);
        addToCommitQueue(context);
        return BranchStatus.PhaseTwo_Committed;
    }

    /**
     * try add context to commitQueue directly, if fail(which means the queue is full),
     * then doBranchCommit urgently(so that the queue could be empty again) and retry this process.
     */
    private void addToCommitQueue(Phase2Context context) {
        if (commitQueue.offer(context)) {
            return;
        }
        CompletableFuture.runAsync(this::doBranchCommitSafely, scheduledExecutor)
                .thenRun(() -> addToCommitQueue(context));
    }

    private void addAllToCommitQueue(List<Phase2Context> contexts) {
        for (Phase2Context context : contexts) {
            addToCommitQueue(context);
        }
    }

    void doBranchCommitSafely() {
        try {
            doBranchCommit();
        } catch (Throwable e) {
            LOGGER.error("Exception occur when doing branch commit", e);
        }
    }

    private void doBranchCommit() {
        if (commitQueue.isEmpty()) {
            return;
        }

        // transfer all context currently received to this list
        List<Phase2Context> allContexts = new LinkedList<>();
        commitQueue.drainTo(allContexts);

        // group context by their resourceId
        Map<String, List<Phase2Context>> groupedContexts = groupedByResourceId(allContexts);

        groupedContexts.forEach(this::dealWithGroupedContexts);
    }

    Map<String, List<Phase2Context>> groupedByResourceId(List<Phase2Context> contexts) {
        Map<String, List<Phase2Context>> groupedContexts = new HashMap<>(DEFAULT_RESOURCE_SIZE);
        contexts.forEach(context -> {
            List<Phase2Context> group = groupedContexts.computeIfAbsent(context.resourceId, key -> new LinkedList<>());
            group.add(context);
        });
        return groupedContexts;
    }

    private void dealWithGroupedContexts(String resourceId, List<Phase2Context> contexts) {
        DataSourceProxy dataSourceProxy = dataSourceManager.get(resourceId);
        if (dataSourceProxy == null) {
            LOGGER.warn("failed to find resource for {} and requeue", resourceId);
            addAllToCommitQueue(contexts);
            return;
        }
        Connection conn = null;
        try {
            conn = dataSourceProxy.getPlainConnection();
            UndoLogManager undoLogManager = UndoLogManagerFactory.getUndoLogManager(dataSourceProxy.getDbType());

            // split contexts into several lists, with each list contain no more element than limit size
            List<List<Phase2Context>> splitByLimit = Lists.partition(contexts, UNDOLOG_DELETE_LIMIT_SIZE);
            for (List<Phase2Context> partition : splitByLimit) {
                // 修改操作数据状态
                try {
                    List<Phase2Context> partitionNew = Lists.newArrayList();
                    for (Phase2Context phase2Context : partition) {
                        if (!undoLogManager.checkUndoLogExist(phase2Context.xid, phase2Context.branchId, conn)) {
                            continue;
                        }
                        partitionNew.add(phase2Context);
                    }
                    if (CollectionUtils.isEmpty(partitionNew)) {
                        continue;
                    }
                    isolateManager.updateDataValidStatus(partitionNew, undoLogManager, conn, TransactionOperationStatus.COMMIT);
                    deleteUndoLog(conn, undoLogManager, partitionNew);
                } catch (Exception e) {
                    e.printStackTrace();
                    conn.rollback();
                    addAllToCommitQueue(contexts);
                }
            }
        } catch (SQLException sqlExx) {
            addAllToCommitQueue(contexts);
            LOGGER.error("failed to get connection for async committing on {} and requeue", resourceId, sqlExx);
        } finally {
            IOUtil.close(conn);
        }

    }

    // 修改数据状态 当提交的情况下。
    private void deleteUndoLog(final Connection conn, UndoLogManager undoLogManager, List<Phase2Context> contexts) {
        Set<String> xids = new LinkedHashSet<>(contexts.size());
        Set<Long> branchIds = new LinkedHashSet<>(contexts.size());
        contexts.forEach(context -> {
            xids.add(context.xid);
            branchIds.add(context.branchId);
        });
        try {
            undoLogManager.batchDeleteUndoLog(xids, branchIds, conn);
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to batch delete undo log", e);
            try {
                conn.rollback();
                addAllToCommitQueue(contexts);
            } catch (SQLException rollbackEx) {
                LOGGER.error("Failed to rollback JDBC resource after deleting undo log failed", rollbackEx);
            }
        }
    }
}
