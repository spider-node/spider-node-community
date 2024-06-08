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
package cn.spider.framework.param.result.build;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ClassUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GlobalUtil
 *
 * @author dds
 */
public class GlobalUtil {

    private static final boolean SUPPORT_VALIDATE = ClassUtils.isPresent("javax.validation.Validator", GlobalUtil.class.getClassLoader());

    public static <T> T notNull(T obj) {
        return obj;
    }

    public static String notBlank(String str) {
        return str;
    }


    public static String format(String str, Object... params) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return MessageFormatter.arrayFormat(str, params).getMessage();
    }


    public static boolean isCollection(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return Iterable.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    /**
     * 排序后子类在前，父类在后
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sortObjExtends(Collection<T> objList) {
        if (CollectionUtils.isEmpty(objList)) {
            return Lists.newArrayList();
        }
        Object[] objs = objList.stream().filter(Objects::nonNull).toArray();
        if (objs.length < 2) {
            return Lists.newArrayList(objList);
        }

        boolean isContinue;
        do {
            isContinue = false;
            for (int i = 0; i < objs.length; i++) {
                for (int j = i; j < objs.length; j++) {
                    if (objs[i].getClass() == objs[j].getClass()) {
                        continue;
                    }
                    if (objs[i].getClass().isAssignableFrom(objs[j].getClass())) {
                        Object o = objs[i];
                        objs[i] = objs[j];
                        objs[j] = o;
                        isContinue = true;
                    }
                }
            }
        } while (isContinue);
        return Arrays.stream(objs).map(t -> (T) t).collect(Collectors.toList());
    }

    public static void traceIdClear(String oldReqId, String reqLogIdKey) {
        if (StringUtils.isNotBlank(oldReqId)) {
            MDC.put(reqLogIdKey, oldReqId);
        } else {
            MDC.remove(reqLogIdKey);
        }
    }
    public static boolean supportValidate() {
        return SUPPORT_VALIDATE;
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY);
    }
}
