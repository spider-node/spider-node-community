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

import cn.spider.framework.transaction.sdk.core.exception.NotSupportYetException;
import cn.spider.framework.transaction.sdk.core.exception.ShouldNeverHappenException;
import cn.spider.framework.transaction.sdk.core.exception.TransactionException;
import cn.spider.framework.transaction.sdk.core.model.AbstractResourceManager;
import cn.spider.framework.transaction.sdk.core.model.BranchStatus;
import cn.spider.framework.transaction.sdk.core.model.BranchType;
import cn.spider.framework.transaction.sdk.core.model.Resource;
import cn.spider.framework.transaction.sdk.datasource.isolate.IsolateManager;
import cn.spider.framework.transaction.sdk.datasource.isolate.TransactionOperationStatus;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogManager;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Data source manager.
 *
 * @author DDS
 */
public class DataSourceManager extends AbstractResourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceManager.class);

    private final AsyncWorker asyncWorker = new AsyncWorker(this);

    private final Map<String, Resource> dataSourceCache = new ConcurrentHashMap<>();

    private final IsolateManager isolateManager = IsolateManager.getIsolateManager();

    /**
     * 锁住查询 使用 xid+resourceId锁住
     *
     * @param branchType
     * @param resourceId
     * @param xid
     * @param lockKeys
     * @return
     * @throws TransactionException
     */
    @Override
    public boolean lockQuery(BranchType branchType, String resourceId, String xid, String lockKeys) throws TransactionException {
        // 使用分布式事务
        return true;
    }

    /**
     * Instantiates a new Data source manager.
     */
    public DataSourceManager() {
    }

    @Override
    public void registerResource(Resource resource) {
        DataSourceProxy dataSourceProxy = (DataSourceProxy) resource;
        dataSourceCache.put(dataSourceProxy.getResourceId(), dataSourceProxy);
    }

    @Override
    public void unregisterResource(Resource resource) {
        throw new NotSupportYetException("unregister a resource");
    }

    /**
     * Get data source proxy.
     *
     * @param resourceId the resource id
     * @return the data source proxy
     */
    public DataSourceProxy get(String resourceId) {
        return (DataSourceProxy) dataSourceCache.get(resourceId);
    }

    @Override
    public BranchStatus branchCommit(BranchType branchType, String xid, long branchId, String resourceId,
                                     String applicationData) throws TransactionException {
        return asyncWorker.branchCommit(xid, branchId, resourceId);
    }

    @Override
    public BranchStatus branchRollback(BranchType branchType, String xid, long branchId, String resourceId,
                                       String applicationData) throws TransactionException {
        DataSourceProxy dataSourceProxy = get(resourceId);
        if (dataSourceProxy == null) {
            throw new ShouldNeverHappenException();
        }
        try {
            // 执行回退
            Connection conn = dataSourceProxy.getPlainConnection();
            UndoLogManager undoLogManager = UndoLogManagerFactory.getUndoLogManager(dataSourceProxy.getDbType());
            // 当不存在 undoLogManager的情况下直接提示成功
            if (!undoLogManager.checkUndoLogExist(xid, branchId, conn)) {
                return BranchStatus.PhaseTwo_Rollbacked;
            }
            isolateManager.rollbackDataValidStatus(undoLogManager, conn, TransactionOperationStatus.ROLL_BACK, xid, branchId);
            UndoLogManagerFactory.getUndoLogManager(dataSourceProxy.getDbType()).undo(dataSourceProxy, xid, branchId, conn);
        } catch (TransactionException | SQLException te) {
            return BranchStatus.PhaseTwo_RollbackFailed_Retryable;
        }
        return BranchStatus.PhaseTwo_Rollbacked;
    }

    @Override
    public Map<String, Resource> getManagedResources() {
        return dataSourceCache;
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.AT;
    }

}
