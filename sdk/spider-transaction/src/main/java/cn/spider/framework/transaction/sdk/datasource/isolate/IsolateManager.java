package cn.spider.framework.transaction.sdk.datasource.isolate;

import cn.spider.framework.transaction.sdk.context.RootContext;
import cn.spider.framework.transaction.sdk.datasource.ConnectionContext;
import cn.spider.framework.transaction.sdk.datasource.ConnectionProxy;
import cn.spider.framework.transaction.sdk.datasource.Phase2Context;
import cn.spider.framework.transaction.sdk.datasource.sql.struct.Field;
import cn.spider.framework.transaction.sdk.datasource.sql.struct.KeyType;
import cn.spider.framework.transaction.sdk.datasource.sql.struct.Row;
import cn.spider.framework.transaction.sdk.datasource.sql.struct.TableRecords;
import cn.spider.framework.transaction.sdk.datasource.undo.BranchUndoLog;
import cn.spider.framework.transaction.sdk.datasource.undo.SQLUndoLog;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogManager;
import cn.spider.framework.transaction.sdk.sqlparser.SQLType;
import cn.spider.framework.transaction.sdk.util.Constants;
import cn.spider.framework.transaction.sdk.util.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @program: flow-cloud
 * @description: 隔离操作
 * @author: dds
 * @create: 2022-07-30 18:34
 */
public class IsolateManager {

    private static IsolateManager ISOLATE_MANAGER = new IsolateManager();

    public void commitBefore(ConnectionProxy cp) throws SQLException {
        ConnectionContext connectionContext = cp.getContext();
        if (!connectionContext.hasUndoLog()) {
            return;
        }

        List<SQLUndoLog> sqlUndoLogs = connectionContext.getUndoItems();
        isolateDataOperation(sqlUndoLogs, cp.getTargetConnection(), RootContext.BRANCH_ID, TransactionOperationStatus.COMMIT);

    }

    public void updateDataValidStatus(List<Phase2Context> partition, UndoLogManager undoLogManager, Connection conn, TransactionOperationStatus status) throws Exception {
        for (Phase2Context phase2Context : partition) {
            BranchUndoLog branchUndoLog = undoLogManager.selectBranchUndoLog(phase2Context.getXid(), phase2Context.getBranchId(), conn);
            if (Objects.isNull(branchUndoLog)) {
                throw new IllegalArgumentException("phase2Context.getXid():" + phase2Context.getXid());
            }
            List<SQLUndoLog> sqlUndoLogs = branchUndoLog.getSqlUndoLogs();
            isolateDataOperation(sqlUndoLogs, conn, RootContext.BRANCH_ID, status);
        }
    }

    /**
     * 回滚的情况下操作
     */
    public void rollbackDataValidStatus(UndoLogManager undoLogManager, Connection conn, TransactionOperationStatus status, String xid, Long branchId) throws SQLException {
        BranchUndoLog branchUndoLog = undoLogManager.selectBranchUndoLog(xid, branchId, conn);
        if (Objects.isNull(branchUndoLog)) {
            throw new IllegalArgumentException("phase2Context.getXid():" + xid);
        }
        List<SQLUndoLog> sqlUndoLogs = branchUndoLog.getSqlUndoLogs();
        isolateDataOperation(sqlUndoLogs, conn, null, status);
    }


    private void isolateDataOperation(List<SQLUndoLog> sqlUndoLogs, Connection conn, String branchId, TransactionOperationStatus operationStatus) throws SQLException {
        for (SQLUndoLog item : sqlUndoLogs) {
            try {
                if (item.getSqlType().equals(SQLType.INSERT) || item.getSqlType().equals(SQLType.UPDATE)) {
                    if (conn.getAutoCommit()) {
                        conn.setAutoCommit(false);
                    }
                    TableRecords afterTableRecords = operationStatus.equals(TransactionOperationStatus.COMMIT) ? item.getAfterImage() : item.getBeforeImage();
                    List<List<Row>> rows = Lists.partition(afterTableRecords.getRows(), 500);
                    for (List<Row> rows1 : rows) {
                        String validDataSql = buildValidDataSql(afterTableRecords, rows1.size());
                        PreparedStatement updatePST = conn.prepareStatement(validDataSql);
                        if (operationStatus.equals(TransactionOperationStatus.COMMIT)) {
                            TableRecords tableRecords = item.getBeforeImage();
                            Row rowAfter = tableRecords.getRows().get(0);
                            String afterBranchId = queryBranchId(rowAfter);
                            if(StringUtils.isNotEmpty(afterBranchId)){
                                Preconditions.checkArgument(!afterBranchId.equals(branchId), "分支事务对应的branchId不一致");
                                Row rowFirst = rows1.get(0);
                                branchId = queryBranchId(rowFirst);
                            }
                        }
                        updatePST.setString(1, branchId);
                        int paramsIndex = 2;
                        for (Row row : rows1) {
                            Field field = row.getFields().stream().filter(items -> items.getKeyType().equals(KeyType.PRIMARY_KEY)).findFirst().get();
                            if (field.getValue() instanceof String) {
                                updatePST.setString(paramsIndex++, (String) field.getValue());
                            } else {
                                updatePST.setLong(paramsIndex++, (Long) field.getValue());
                            }
                        }
                        updatePST.executeUpdate();
                    }
                }
            } catch (Exception throwables) {
                try {
                    conn.rollback();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                throw throwables;
            }
        }
    }

    private String queryBranchId(Row row) {
        Field field = row.getFields().stream().filter(items -> items.getName().equals(Constants.BRANCH_ID)).findFirst().get();
        return Objects.isNull(field.getValue()) ? null : (String) field.getValue();
    }

    private String buildValidDataSql(TableRecords afterTableRecords, int size) {
        StringBuilder sqlBuilder = new StringBuilder(64);
        sqlBuilder.append("UPDATE ").append(afterTableRecords.getTableName()).append(" SET  ").append("branch_id = ?");
        sqlBuilder.append(" WHERE ").append("ID").append(" IN ");
        appendInParam(size, sqlBuilder);
        return sqlBuilder.toString();

    }

    protected static void appendInParam(int size, StringBuilder sqlBuilder) {
        sqlBuilder.append(" (");
        for (int i = 0; i < size; i++) {
            sqlBuilder.append("?");
            if (i < (size - 1)) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(") ");
    }

    public static IsolateManager getIsolateManager() {
        return ISOLATE_MANAGER;
    }
}
