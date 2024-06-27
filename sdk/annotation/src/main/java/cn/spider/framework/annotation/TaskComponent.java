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

import org.apache.commons.lang3.StringUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskComponent {

    /**
     * 服务组件类名称，默认为类名
     *
     * @return name
     */
    String name() default StringUtils.EMPTY;

    String workerName() default StringUtils.EMPTY;

    /**
     * 扫描父类文件
     *
     * @return 默认 true 除当前类文件，也会扫描父类中的服务节点。为 false 时只会扫描当前类中的服务节点
     */
    boolean scanSuper() default true;
}
