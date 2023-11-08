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
package cn.spider.framework.transaction.sdk.datasource.exec;

import cn.spider.framework.transaction.sdk.context.GlobalLockConfigHolder;
import cn.spider.framework.transaction.sdk.core.model.GlobalLockConfig;


/**
 * Lock retry controller
 *
 * @author DDS
 */
public class LockRetryController {

    private static final GlobalConfig LISTENER = new GlobalConfig();

    private int lockRetryInterval;

    private int lockRetryTimes;

    /**
     * Instantiates a new Lock retry controller.
     */
    public LockRetryController() {
        this.lockRetryInterval = getLockRetryInterval();
        this.lockRetryTimes = getLockRetryTimes();
    }

    /**
     * Sleep.
     *
     * @param e the e
     * @throws LockWaitTimeoutException the lock wait timeout exception
     */
    public void sleep(Exception e) throws LockWaitTimeoutException {
        if (--lockRetryTimes < 0) {
            throw new LockWaitTimeoutException("Global lock wait timeout", e);
        }

        try {
            Thread.sleep(lockRetryInterval);
        } catch (InterruptedException ignore) {
        }
    }

    int getLockRetryInterval() {
        // get customized config first
        GlobalLockConfig config = GlobalLockConfigHolder.getCurrentGlobalLockConfig();
        if (config != null) {
            int configInterval = config.getLockRetryInterval();
            if (configInterval > 0) {
                return configInterval;
            }
        }
        // if there is no customized config, use global config instead
        return 20;
    }

    int getLockRetryTimes() {
        // get customized config first
        GlobalLockConfig config = GlobalLockConfigHolder.getCurrentGlobalLockConfig();
        if (config != null) {
            int configTimes = config.getLockRetryTimes();
            if (configTimes >= 0) {
                return configTimes;
            }
        }
        // if there is no customized config, use global config instead
        return 20;
    }

    static class GlobalConfig  {




    }
}
