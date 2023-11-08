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
package cn.spider.framework.annotation;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在服务节点的入参变量上，用来从 StoryBus 的 req、sta、var 域获取变量值，直接赋值给该参数变量
 *
 * @author DDS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface TaskParam {

    /**
     * 字段名称
     *
     * @return value
     */
    String value() default StringUtils.EMPTY;

    /**
     * 作用域
     * @see ScopeTypeEnum
     *
     * @return 作用域
     */
    ScopeTypeEnum scopeEnum() default ScopeTypeEnum.VARIABLE;
}
