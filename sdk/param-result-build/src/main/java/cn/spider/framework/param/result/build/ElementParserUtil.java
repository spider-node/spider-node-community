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

import cn.spider.framework.annotation.*;
import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * ElementParserUtil
 *
 * @author dds
 */
public class ElementParserUtil {

    /**
     * 基本数据类型默认值
     */
    private static final Map<Class<?>, Object> PRIMITIVE_INIT_MAP = Maps.newHashMap();

    static {
        PRIMITIVE_INIT_MAP.put(long.class, 0L);
        PRIMITIVE_INIT_MAP.put(int.class, 0);
        PRIMITIVE_INIT_MAP.put(short.class, (short) 0);
        PRIMITIVE_INIT_MAP.put(byte.class, (byte) 0);
        PRIMITIVE_INIT_MAP.put(char.class, (char) 0);
        PRIMITIVE_INIT_MAP.put(double.class, 0.0);
        PRIMITIVE_INIT_MAP.put(float.class, 0.0f);
        PRIMITIVE_INIT_MAP.put(boolean.class, false);
    }

    public static Object initPrimitive(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return PRIMITIVE_INIT_MAP.get(clazz);
    }

    public static Optional<Object> newInstance(Class<?> clazz) {
        if (clazz == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(clazz.getDeclaredConstructor().newInstance());
        } catch (Throwable e) {
           return Optional.empty();
        }
    }

    public static Optional<MethodWrapper.TaskFieldProperty> getTaskParamAnnotation(Parameter p, String paramName) {
        if (p == null) {
            return Optional.empty();
        }

        TaskParam taskParamAnn = p.getAnnotation(TaskParam.class);
        if (taskParamAnn != null) {
            if (StringUtils.isNotBlank(taskParamAnn.value())) {
                paramName = taskParamAnn.value();
            }
            return Optional.of(new MethodWrapper.TaskFieldProperty(paramName, taskParamAnn.scopeEnum()));
        }

        ReqTaskParam reqTaskParamAnn = p.getAnnotation(ReqTaskParam.class);
        if (reqTaskParamAnn != null) {
            if (StringUtils.isNotBlank(reqTaskParamAnn.value())) {
                paramName = reqTaskParamAnn.value();
            }
            MethodWrapper.TaskFieldProperty taskFieldProperty = new MethodWrapper.TaskFieldProperty(paramName, ScopeTypeEnum.REQUEST);
            taskFieldProperty.setInjectSelf(reqTaskParamAnn.reqSelf());
            return Optional.of(taskFieldProperty);
        }

        StaTaskParam staTaskParamAnn = p.getAnnotation(StaTaskParam.class);
        if (staTaskParamAnn != null) {
            if (StringUtils.isNotBlank(staTaskParamAnn.value())) {
                paramName = staTaskParamAnn.value();
            }
            return Optional.of(new MethodWrapper.TaskFieldProperty(paramName, ScopeTypeEnum.STABLE));
        }

        VarTaskParam varTaskParamAnn = p.getAnnotation(VarTaskParam.class);
        if (varTaskParamAnn != null) {
            if (StringUtils.isNotBlank(varTaskParamAnn.value())) {
                paramName = varTaskParamAnn.value();
            }
            return Optional.of(new MethodWrapper.TaskFieldProperty(paramName, ScopeTypeEnum.VARIABLE));
        }
        return Optional.empty();
    }

    public static List<ParamInjectDef> getFieldInjectDefList(Class<?> clazz) {
        List<ParamInjectDef> fieldInjectDefList = Lists.newArrayList();
        if (clazz == null) {
            return fieldInjectDefList;
        }

        Set<Field> fieldSet = Sets.newHashSet();
        Field[] taskFields = FieldUtils.getFieldsWithAnnotation(clazz, TaskField.class);
        if (ArrayUtils.isNotEmpty(taskFields)) {
            Stream.of(taskFields).forEach(field -> {
                if (fieldSet.contains(field)) {
                    return;
                }
                fieldSet.add(field);
                TaskField taskFieldAnn = field.getAnnotation(TaskField.class);
                String targetName = Optional.of(taskFieldAnn).map(TaskField::value).filter(StringUtils::isNotBlank).orElse(field.getName());
                MethodWrapper.TaskFieldProperty taskFieldProperty = new MethodWrapper.TaskFieldProperty(targetName, taskFieldAnn.scopeEnum());
                ParamInjectDef injectDef = new ParamInjectDef(GlobalConstant.STORY_DATA_SCOPE.contains(taskFieldProperty.getScopeDataEnum()), field.getType(), field.getName(), taskFieldProperty);
                fieldInjectDefList.add(injectDef);
            });
        }

        Field[] reqTaskFields = FieldUtils.getFieldsWithAnnotation(clazz, ReqTaskField.class);
        if (ArrayUtils.isNotEmpty(reqTaskFields)) {
            Stream.of(reqTaskFields).forEach(field -> {
                if (fieldSet.contains(field)) {
                    return;
                }
                fieldSet.add(field);
                ReqTaskField taskFieldAnn = field.getAnnotation(ReqTaskField.class);
                String targetName = Optional.of(taskFieldAnn).map(ReqTaskField::value).filter(StringUtils::isNotBlank).orElse(field.getName());
                MethodWrapper.TaskFieldProperty taskFieldProperty = new MethodWrapper.TaskFieldProperty(targetName, ScopeTypeEnum.REQUEST);
                ParamInjectDef injectDef = new ParamInjectDef(true, field.getType(), field.getName(), taskFieldProperty);
                fieldInjectDefList.add(injectDef);
            });
        }

        Field[] staTaskFields = FieldUtils.getFieldsWithAnnotation(clazz, StaTaskField.class);
        if (ArrayUtils.isNotEmpty(staTaskFields)) {
            Stream.of(staTaskFields).forEach(field -> {
                if (fieldSet.contains(field)) {
                    return;
                }
                fieldSet.add(field);
                StaTaskField taskFieldAnn = field.getAnnotation(StaTaskField.class);
                String targetName = Optional.of(taskFieldAnn).map(StaTaskField::value).filter(StringUtils::isNotBlank).orElse(field.getName());
                MethodWrapper.TaskFieldProperty taskFieldProperty = new MethodWrapper.TaskFieldProperty(targetName, ScopeTypeEnum.STABLE);
                ParamInjectDef injectDef = new ParamInjectDef(true, field.getType(), field.getName(), taskFieldProperty);
                fieldInjectDefList.add(injectDef);
            });
        }

        Field[] varTaskFields = FieldUtils.getFieldsWithAnnotation(clazz, VarTaskField.class);
        if (ArrayUtils.isNotEmpty(varTaskFields)) {
            Stream.of(varTaskFields).forEach(field -> {
                if (fieldSet.contains(field)) {
                    return;
                }
                fieldSet.add(field);
                VarTaskField taskFieldAnn = field.getAnnotation(VarTaskField.class);
                String targetName = Optional.of(taskFieldAnn).map(VarTaskField::value).filter(StringUtils::isNotBlank).orElse(field.getName());
                MethodWrapper.TaskFieldProperty taskFieldProperty = new MethodWrapper.TaskFieldProperty(targetName, ScopeTypeEnum.VARIABLE);
                ParamInjectDef injectDef = new ParamInjectDef(true, field.getType(), field.getName(), taskFieldProperty);
                fieldInjectDefList.add(injectDef);
            });
        }
        return fieldInjectDefList;
    }

    public static boolean isAssignable(Class<?> left, Class<?> right) {
        if (left == null || right == null) {
            return false;
        }
        if (left == right) {
            return true;
        }
        if (left.isAssignableFrom(right)) {
            return true;
        }
        try {
            if (left.isPrimitive() && !right.isPrimitive()) {
                return right.getField("TYPE").get(null) == left;
            } else if (!left.isPrimitive() && right.isPrimitive()) {
                return left.getField("TYPE").get(null) == right;
            }
        } catch (Throwable e) {
            // ignore
        }
        return false;
    }

    public static boolean isSpringInitialization(Class<?> clazz) {
        if (clazz == null || clazz.isPrimitive()) {
            return false;
        }
        return AnnotationUtils.findAnnotation(clazz, SpringInitialization.class) != null;
    }

    public static boolean isValidDataExpression(String expression) {
        if (StringUtils.isBlank(expression)) {
            return false;
        }
        if (Objects.equals(ScopeTypeEnum.RESULT.getKey(), expression)) {
            return true;
        }
        return Pattern.matches(GlobalConstant.VALID_DATA_EXPRESSION_PATTERN, expression);
    }
}
