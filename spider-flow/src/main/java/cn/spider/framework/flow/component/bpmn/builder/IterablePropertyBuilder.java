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
package cn.spider.framework.flow.component.bpmn.builder;

import cn.spider.framework.flow.bpmn.enums.IterateStrategyEnum;
import cn.spider.framework.flow.bpmn.impl.BasicElementIterable;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ElementParserUtil;

/**
 * @author lykan
 */
public class IterablePropertyBuilder {

    private final BasicElementIterable elementIterable;

    public IterablePropertyBuilder(String iteSource) {
        AssertUtil.isTrue(ElementParserUtil.isValidDataExpression(iteSource),
                ExceptionEnum.BPMN_ATTRIBUTE_INVALID, "The set ite-source being iterated over is invalid. iteSource: {}", iteSource);
        this.elementIterable = new BasicElementIterable();
        this.elementIterable.setIteSource(iteSource);
    }

    public IterablePropertyBuilder openAsync() {
        elementIterable.setOpenAsync(true);
        return this;
    }

    public IterablePropertyBuilder iteStrategy(IterateStrategyEnum strategy) {
        elementIterable.setIteStrategy(strategy);
        return this;
    }

    public IterablePropertyBuilder stride(Integer stride) {
        elementIterable.setStride(stride);
        return this;
    }

    public BasicElementIterable build() {
        return elementIterable;
    }
}
