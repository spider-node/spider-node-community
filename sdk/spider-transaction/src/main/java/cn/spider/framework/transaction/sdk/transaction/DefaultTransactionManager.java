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
package cn.spider.framework.transaction.sdk.transaction;
import cn.spider.framework.transaction.sdk.context.ContextCore;
import cn.spider.framework.transaction.sdk.context.ContextCoreLoader;
import cn.spider.framework.transaction.sdk.core.exception.TransactionException;
import cn.spider.framework.transaction.sdk.core.model.GlobalStatus;
import cn.spider.framework.transaction.sdk.core.model.TransactionManager;

/**
 * The type Default transaction manager.
 *
 * @author DDS
 */
public class DefaultTransactionManager implements TransactionManager {

    private static ContextCore CONTEXT_HOLDER = ContextCoreLoader.load();

    private static final String XID_KEY = "transactionXid";

    /**
     * 重构----------- 开启事务由spider来控制以及返回xid
     * @param applicationId           ID of the application who begins this transaction.
     * @param transactionServiceGroup ID of the transaction service group.
     * @param name                    Give a name to the global transaction.
     * @param timeout                 Timeout of the global transaction.
     * @return
     * @throws TransactionException
     */
    @Override
    public String begin(String applicationId, String transactionServiceGroup, String name, int timeout)
        throws TransactionException {
        // 返回事务id，该id从 threadLocal里面拿
        return (String) CONTEXT_HOLDER.get(XID_KEY);
    }

    /**
     * 通知spider 提升提交通过，
     * @param xid XID of the global transaction.
     * @return
     * @throws TransactionException
     */
    @Override
    public GlobalStatus commit(String xid) throws TransactionException {
        CONTEXT_HOLDER.put(XID_KEY,xid);
        return GlobalStatus.Committing ;//response.getGlobalStatus();
    }

    /**
     * 通知spider回滚状态
     * @param xid XID of the global transaction
     * @return
     * @throws TransactionException
     */
    @Override
    public GlobalStatus rollback(String xid) throws TransactionException {
        // 通知回滚
        return GlobalStatus.Rollbacking; //response.getGlobalStatus();
    }

    @Override
    public GlobalStatus getStatus(String xid) throws TransactionException {

        return null ; //response.getGlobalStatus();
    }

    @Override
    public GlobalStatus globalReport(String xid, GlobalStatus globalStatus) throws TransactionException {

        return null;
    }
}
