/*
 *
 *  * Copyright (c) 2020-2023, Lykan (jiashuomeng@gmail.com).
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package cn.spider.framework.flow.bus;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.db.map.RocksdbBusinessMap;
import cn.spider.framework.db.rocksdb.RocksdbKeyManager;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.load.loader.ClassLoaderManager;

import java.util.Map;

/**
 * InScopeData
 *
 * @author lykan
 */
public class InScopeData extends RocksdbBusinessMap<Object, Object> implements ScopeData {

    private final ScopeTypeEnum scopeTypeEnum;

    /**
     * 该类的线程安全map，修改为rocksdb-防止丢失，防止生命周期过长，并发过高，导致，内存暴涨。
     *
     * @param scopeTypeEnum
     * @param requestId
     */
    public InScopeData(ScopeTypeEnum scopeTypeEnum, String requestId) {
        super(SpiderCoreVerticle.factory.getBean(RocksdbUtil.class),requestId,
                SpiderCoreVerticle.factory.getBean("classLoaderMap", Map.class),
                SpiderCoreVerticle.factory.getBean(RocksdbKeyManager.class),
                SpiderCoreVerticle.factory.getBean(ClassLoaderManager.class));
        this.scopeTypeEnum = scopeTypeEnum;
    }

    @Override
    public ScopeTypeEnum getScopeDataEnum() {
        return this.scopeTypeEnum;
    }
}
