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
package cn.spider.framework.flow.util;

import cn.spider.framework.flow.enums.ExceptionTypeEnum;
import cn.spider.framework.flow.exception.*;
import org.apache.commons.lang3.StringUtils;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author lykan
 */
public class ExceptionUtil {

    private static final Map<String, ExceptionEnum> EXCEPTION_ENUM_MAP =
            Arrays.stream(ExceptionEnum.values()).collect(Collectors.toMap(ExceptionEnum::getExceptionCode, Function.identity(), (x, y) -> y));

    public static KstryException buildException(Throwable exception, @Nonnull ExceptionEnum exceptionEnum, String desc) {
        if (exception instanceof KstryException) {
            return (KstryException) exception;
        }
        if (StringUtils.isBlank(desc)) {
            desc = exceptionEnum.getDesc();
        }

        ExceptionTypeEnum typeEnum = exceptionEnum.getTypeEnum();
        switch (typeEnum) {
            case CONFIG:
                return new ResourceException(exceptionEnum.getExceptionCode(), desc, exception);
            case ASYNC_TASK:
                if (exceptionEnum == ExceptionEnum.ASYNC_TASK_TIMEOUT) {
                    return new TaskTimeoutException(exceptionEnum.getExceptionCode(), desc, exception);
                }
                return new TaskAsyncException(exceptionEnum.getExceptionCode(), desc, exception);
            case COMPONENT:
                return new KstryComponentException(exceptionEnum.getExceptionCode(), desc, exception);
            case NODE_INVOKE:
                return new NodeInvokeException(exceptionEnum.getExceptionCode(), desc, exception);
            case STORY:
                return new StoryException(exceptionEnum.getExceptionCode(), desc, exception);
            default:
                return new KstryException(exceptionEnum.getExceptionCode(), desc, exception);
        }
    }

    public static String getExcMessage(String code, String desc) {
        String defErrorDesc = "System Error!";
        if (code == null || EXCEPTION_ENUM_MAP.get(code) == null) {
            return StringUtils.isBlank(desc) ? defErrorDesc : desc;
        }
        return GlobalUtil.format("[{}] {}", StringUtils.isBlank(code) ? ExceptionEnum.SYSTEM_ERROR.getExceptionCode() : code, StringUtils.isBlank(desc) ? defErrorDesc : desc);
    }

    public static Optional<String> tryGetCode(Throwable throwable) {
        if (throwable instanceof KstryException) {
            return Optional.ofNullable(GlobalUtil.transferNotEmpty(throwable, KstryException.class).getErrorCode());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getErrFromCause(Throwable err, Class<T> clazz) {
        if (err == null || clazz == null) {
            return Optional.empty();
        }

        Throwable e = err;
        while (e != null) {
            if (clazz.isAssignableFrom(e.getClass())) {
                return Optional.of((T) e);
            }
            e = e.getCause();
        }
        return Optional.empty();
    }
}
