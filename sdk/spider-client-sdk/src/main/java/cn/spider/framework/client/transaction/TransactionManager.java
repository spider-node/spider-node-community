package cn.spider.framework.client.transaction;

import cn.spider.framework.transaction.sdk.core.exception.TransactionException;
import cn.spider.framework.transaction.sdk.datasource.util.JdbcUtils;
import org.springframework.beans.factory.InitializingBean;

import java.sql.SQLException;

/**
 * @program: spider-node
 * @description: 事务管理器
 * @author: dds
 * @create: 2023-03-06 13:55
 */
public class TransactionManager {

    // 获取 dataSource.url

    private SpiderTransactionOperation operation;

    //private String dbType;

    public TransactionManager(SpiderTransactionOperation operation) {
        this.operation = operation;
    }


    /**
     * 提交事务
     *
     * @param xid,brushId
     * @throws TransactionException
     */
    public void commit(String xid, Long branchId, String resourceId) throws TransactionException {
        // 当 xid与brushId不存在的情况下，直接return
        TransactionOperateModel operateModel = new TransactionOperateModel();
        operateModel.setXid(xid);
        operateModel.setBranchId(branchId);
        operateModel.setResourceId(resourceId);
        operation.commit(operateModel);
    }

    /**
     * 回滚事务
     *
     * @param xid
     * @throws TransactionException
     */
    public void rollBack(String xid, Long branchId, String resourceId) throws TransactionException {
        // 当 xid与brushId不存在的情况下，直接return
        TransactionOperateModel operateModel = new TransactionOperateModel();
        operateModel.setXid(xid);
        operateModel.setBranchId(branchId);
        operateModel.setResourceId(resourceId);
        operation.rollBack(operateModel);
    }
}
