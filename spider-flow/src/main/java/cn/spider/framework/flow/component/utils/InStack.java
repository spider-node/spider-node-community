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
package cn.spider.framework.flow.component.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author lykan
 */
public interface InStack<T> {

    /**
     * push 单个元素
     *
     * @param t 元素
     */
    void push(T t);

    /**
     * push 有序集合
     *
     * @param list list
     */
    void pushList(List<T> list);

    /**
     * push 集合，集合是否有序无要求
     *
     * @param collection collection
     */
    void pushCollection(Collection<T> collection);

    /**
     * 探出元素
     *
     * @return 元素
     */
    Optional<T> pop();

    /**
     * 拿到下个元素，但是元素不探出
     *
     * @return 下个元素
     */
    Optional<T> peek();

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    boolean isEmpty();
}
