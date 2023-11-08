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
package cn.spider.framework.flow.container.task.impl;

import cn.spider.framework.flow.container.task.TaskComponentRegister;
import cn.spider.framework.flow.util.AssertUtil;

/**
 * @author lykan
 */
public class TaskComponentProxy implements TaskComponentRegister {


    private final String name;

    public TaskComponentProxy(String taskComponentName) {
        this.name = taskComponentName;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
