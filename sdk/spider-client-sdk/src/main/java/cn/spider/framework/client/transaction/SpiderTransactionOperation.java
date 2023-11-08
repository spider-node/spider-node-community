package cn.spider.framework.client.transaction;
import cn.spider.framework.transaction.sdk.core.exception.TransactionException;
import cn.spider.framework.transaction.sdk.core.model.BranchType;
import cn.spider.framework.transaction.sdk.core.model.DefaultResourceManager;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogManager;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogManagerFactory;
import org.springframework.stereotype.Component;

/**
 * @program: flow-cloud
 * @description: 事务集中处理部分
 * @author: dds
 * @create: 2022-07-29 23:03
 */

public class SpiderTransactionOperation {

    private DefaultResourceManager defaultResourceManager = DefaultResourceManager.get();

    public void commit(TransactionOperateModel operateModel) throws TransactionException {

        // 根据 branchId xid查询是否存在
        defaultResourceManager.branchCommit(BranchType.AT,operateModel.getXid(),Long.parseLong(operateModel.getBranchId()), operateModel.getResourceId(),null);
    }

    public void rollBack(TransactionOperateModel operateModel) throws TransactionException {
        // 根据 branchId xid查询是否存在
        defaultResourceManager.branchRollback(BranchType.AT,operateModel.getXid(),Long.parseLong(operateModel.getBranchId()),operateModel.getResourceId(),null);
    }
}
