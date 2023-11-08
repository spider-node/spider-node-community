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
package cn.spider.framework.transaction.sdk.datasource.undo.parser;

import cn.spider.framework.transaction.sdk.executor.Initialize;
import cn.spider.framework.transaction.sdk.loader.LoadLevel;
import cn.spider.framework.transaction.sdk.util.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import cn.spider.framework.transaction.sdk.datasource.undo.BranchUndoLog;
import cn.spider.framework.transaction.sdk.datasource.undo.UndoLogParser;

/**
 * The type Json based undo log parser.
 *
 * @author DDS
 */
@LoadLevel(name = FastjsonUndoLogParser.NAME)
public class FastjsonUndoLogParser implements UndoLogParser, Initialize {

    public static final String NAME = "fastjson";

    private final SimplePropertyPreFilter filter = new SimplePropertyPreFilter();

    @Override
    public void init() {
        filter.getExcludes().add("tableMeta");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] getDefaultContent() {
        return "{}".getBytes(Constants.DEFAULT_CHARSET);
    }

    @Override
    public byte[] encode(BranchUndoLog branchUndoLog) {
        System.out.println("encode11111111111"+branchUndoLog);
        String json = JSON.toJSONString(branchUndoLog, filter, SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat);
        return json.getBytes(Constants.DEFAULT_CHARSET);
    }

    @Override
    public BranchUndoLog decode(byte[] bytes) {
        String text = new String(bytes, Constants.DEFAULT_CHARSET);
        return JSON.parseObject(text, BranchUndoLog.class);
    }
}
