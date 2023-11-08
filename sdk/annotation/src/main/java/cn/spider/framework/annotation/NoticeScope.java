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
 *
 * 可以标注在服务节点的返回值类上或类字段上，也可与 cn.kstry.framework.core.bpmn.ServiceTask 联用标注在服务节点上
 *
 * 默认情况下指定服务节点返回值或字段变量被通知到 bus 中的 stable 和 variable 两个变量集中，如果指定 scope 以指定域为准
 *
 * @author DDS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface NoticeScope {

    /**
     * 通知字段名称
     *
     * @return target
     */
    String target() default StringUtils.EMPTY;

    /**
     *  指定结果到哪些 Scope。标注在方法上且该参数不为空时会使 Response Class 上的 @Notice... 类注解失效
     *
     * @return noticeScope
     */
    ScopeTypeEnum[] scope() default {};
}
