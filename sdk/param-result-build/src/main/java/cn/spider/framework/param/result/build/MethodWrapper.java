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

import cn.spider.framework.annotation.NoticeScope;
import cn.spider.framework.annotation.TaskService;
import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.common.config.Constant;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author dds
 */
@Slf4j
public class MethodWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodWrapper.class);

    private final Method method;

    private final ReturnTypeNoticeDef returnTypeNoticeDef = new ReturnTypeNoticeDef();

    private List<ParamInjectDef> paramInjectDefs;

    private final String kvScope;

    private final NoticeAnnotationWrapper noticeMethodSpecify;

    private final String ability;

    private boolean monoResult;

    private final boolean isCustomRole;

    private final TaskInstructWrapper taskInstructWrapper;

    public MethodWrapper(@Nonnull Method method, @Nonnull TaskService annotation,
                         @Nonnull NoticeAnnotationWrapper noticeMethodSpecify, TaskInstructWrapper taskInstructWrapper, boolean isCustomRole) {
        this.method = method;
        this.kvScope = annotation.kvScope();
        this.noticeMethodSpecify = noticeMethodSpecify;
        this.ability = annotation.ability();
        this.monoResult = false;
        this.isCustomRole = isCustomRole;
        this.taskInstructWrapper = taskInstructWrapper;
        methodParser(method);
    }

    public Method getMethod() {
        return method;
    }

    public ReturnTypeNoticeDef getReturnTypeNoticeDef() {
        return returnTypeNoticeDef;
    }

    public List<ParamInjectDef> getParamInjectDefs() {
        return paramInjectDefs;
    }

    public String getKvScope() {
        return kvScope;
    }

    public String getAbility() {
        return ability;
    }

    public boolean isMonoResult() {
        return monoResult;
    }

    public boolean isCustomRole() {
        return isCustomRole;
    }

    public Optional<TaskInstructWrapper> getTaskInstructWrapper() {
        return Optional.ofNullable(taskInstructWrapper);
    }

    private void methodParser(Method method) {
        Class<?> returnType = method.getReturnType();
        if (!Objects.equals(Constant.VOID, returnType.getName())) {
            returnTypeParserNew(returnType);
        }
        Parameter[] parameters = method.getParameters();
        // 获取到对应的接口-方法参数名称
        String[] parameterNames = queryParameterNames(parameters);

        if (ArrayUtils.isNotEmpty(parameters)) {
            parametersParser(parameters, parameterNames);
        }
    }

    /**
     * 获取参数上 列表的参数
     *
     * @return
     */
    public String[] queryParameterNames(Parameter[] parameters) {
        String[] paramNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            paramNames[i] = p.getName();
        }
        return paramNames;
    }

    private void parametersParser(Parameter[] parameters, String[] parameterNames) {
        ParamInjectDef[] injectDefs = new ParamInjectDef[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            injectDefs[i] = null;
            Optional<TaskFieldProperty> annOptional = ElementParserUtil.getTaskParamAnnotation(p, parameterNames[i]);
            if (GlobalUtil.isCollection(p.getType())) {
                if (!annOptional.isPresent()) {
                    continue;
                }
                boolean needInject = GlobalConstant.STORY_DATA_SCOPE.contains(annOptional.get().getScopeDataEnum());
                injectDefs[i] = new ParamInjectDef(needInject, p.getType(), parameterNames[i], annOptional.get());
                continue;
            }

            List<ParamInjectDef> injectDefList = getFieldInjectDefs(p.getType());
            if (annOptional.isPresent() || CollectionUtils.isNotEmpty(injectDefList)
                    || p.getType().isPrimitive()) {
                boolean needInject = !annOptional.isPresent() || GlobalConstant.STORY_DATA_SCOPE.contains(annOptional.get().getScopeDataEnum());
                ParamInjectDef injectDef = new ParamInjectDef(needInject, p.getType(), parameterNames[i], annOptional.orElse(null));
                injectDef.setFieldInjectDefList(injectDefList);
                injectDefs[i] = injectDef;
            } else {
                ParamInjectDef injectDef = new ParamInjectDef(false, p.getType(), parameterNames[i], null);
                injectDefs[i] = injectDef;
            }
        }
        for(int i = 0; i < injectDefs.length; i++){
            ParamInjectDef injectDef = injectDefs[i];
            if(injectDef.notNeedInject()){
                continue;
            }
            this.paramInjectDefs = injectDef.getFieldInjectDefList();
        }
    }

    private List<ParamInjectDef> getFieldInjectDefs(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return null;
        }
        return ElementParserUtil.getFieldInjectDefList(clazz);
    }

    private void returnTypeParser(Class<?> returnType) {

        String className = StringUtils.uncapitalize(returnType.getSimpleName());
        // 加载方法上面的注解信息
        parseReturnType(noticeMethodSpecify, returnType, className);
        // 获取字段上面的字段上的注解信息
        List<Field> fieldsList = FieldUtils.getAllFieldsList(returnType);
        if (CollectionUtils.isNotEmpty(fieldsList)) {
            fieldsList.forEach(field -> {
                NoticeAnnotationWrapper fieldNoticeAnn = new NoticeAnnotationWrapper(field);
                parseReturnType(fieldNoticeAnn, field.getType(), field.getName());
            });
        }
    }

    private void returnTypeParserNew(Class<?> returnType) {
        List<Field> fieldsList = FieldUtils.getAllFieldsList(returnType);
        List<NodeField> nodeFields = new ArrayList<>();
        if (noticeMethodSpecify.getNoticeScope().isPresent()) {
            // 说明在方法上面有注解，那么用方面上面的注解为p0

            NoticeScope noticeScope = noticeMethodSpecify.getNoticeScope().get();
            for (Field field : fieldsList) {
                String finalTargetName = noticeScope.target() + "." + field.getName();
                String fieldType = queryFieldType(field.getType());
                NodeField nodeField = new NodeField(field.getName(), finalTargetName, fieldType);
                List<NodeObjectStructure> nodeObjectStructures = queryListInfoByField(field);
                nodeField.setNodeParamStructure(nodeObjectStructures);
                nodeFields.add(nodeField);
            }
            returnTypeNoticeDef.setNodeFields(nodeFields);
            return;
        }
        // 加载方法上面的注解信息
        // 获取字段上面的字段上的注解信息 为p1
        if (CollectionUtils.isNotEmpty(fieldsList)) {
            for (Field field : fieldsList) {
                String finalTargetName = buildFinalTargetName(field);
                String fieldType = queryFieldType(field.getType());
                NodeField nodeField = new NodeField(field.getName(), finalTargetName, fieldType);
                List<NodeObjectStructure> nodeObjectStructures = queryListInfoByField(field);
                nodeField.setNodeParamStructure(nodeObjectStructures);
                nodeFields.add(nodeField);
            }
            returnTypeNoticeDef.setNodeFields(nodeFields);
        }
    }

    private String buildFinalTargetName(Field field) {
        NoticeAnnotationWrapper fieldNoticeAnn = new NoticeAnnotationWrapper(field);
        if (fieldNoticeAnn.getNoticeSta().isPresent()) {
            return fieldNoticeAnn.getNoticeSta().get().target();
        } else if (fieldNoticeAnn.getNoticeVar().isPresent()) {
            return fieldNoticeAnn.getNoticeVar().get().target();
        } else if (fieldNoticeAnn.getNoticeScope().isPresent()) {
            return fieldNoticeAnn.getNoticeScope().get().target();
        } else {
            return null;
        }
    }

    public List<NodeObjectStructure> queryListInfoByField(Field field) {
        List<NodeObjectStructure> structures = new ArrayList<>();
        if (List.class.isAssignableFrom(field.getType())) {
            // 说明字段类似为List
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                // 获取实际类型参数数组
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                // 假设只有一个类型参数，并且它是Class类型
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                    Class<?> sonClass = (Class<?>) actualTypeArguments[0];
                    // 获取Student类的所有字段
                    Field[] sonFields = sonClass.getDeclaredFields();
                    for (Field f : sonFields) {
                        NodeObjectStructure structure = new NodeObjectStructure(queryFieldType(f.getType()), f.getName());
                        structures.add(structure);
                    }
                }

            }
        } else if (queryFieldType(field.getType()).equals("java.lang.Object")) {
            List<Field> sonfieldsList = FieldUtils.getAllFieldsList(field.getType());
            for (Field sonField : sonfieldsList) {
                NodeObjectStructure structure = new NodeObjectStructure(queryFieldType(sonField.getType()), sonField.getName());
                structures.add(structure);
            }
        }
        return structures;
    }

    /**
     * 实现Field 判断是否为实体类，如
     *
     * @param fieldClass
     * @return
     */

    public String queryFieldType(Class fieldClass) {
        // 判断fieldClass如果是枚举类型
        if (fieldClass.isEnum()) {
            return "java.lang.String";
        }
        // 如果 fieldClass的类型不是String,Integer,Long,Float,Double,Boolean,Date,BigDecimal,LocalDateTime,这些类型就执行改为Object
        if (!(fieldClass.equals(String.class) || fieldClass.equals(Integer.class) ||
                fieldClass.equals(Long.class) || fieldClass.equals(Float.class) ||
                fieldClass.equals(Double.class) || fieldClass.equals(Boolean.class) ||
                fieldClass.equals(Date.class) || fieldClass.equals(BigDecimal.class) ||
                fieldClass.equals(LocalDateTime.class))) {
            return "java.lang.Object";
        }
        return fieldClass.getTypeName();
    }


    private void noticeScopeDef(NoticeScope annotation, NoticeFieldItem noticeFieldItem) {
        if (ArrayUtils.isEmpty(annotation.scope())) {
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
            return;
        }
        List<ScopeTypeEnum> scopeTypeList = Lists.newArrayList(annotation.scope());
        if (scopeTypeList.contains(ScopeTypeEnum.STABLE)) {
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
        }
        if (scopeTypeList.contains(ScopeTypeEnum.VARIABLE)) {
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
        }
    }

    private void parseReturnType(NoticeAnnotationWrapper noticeAnn, Class<?> returnType, String fieldName) {

        noticeAnn.getNoticeSta().ifPresent(noticeSta -> {
            NoticeFieldItem noticeFieldItem = new NoticeFieldItem(fieldName, noticeSta.target(), returnType, noticeAnn.isNotField());
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
        });
        noticeAnn.getNoticeVar().ifPresent(noticeVar -> {
            NoticeFieldItem noticeFieldItem = new NoticeFieldItem(fieldName, noticeVar.target(), returnType, noticeAnn.isNotField());
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
        });
        noticeAnn.getNoticeScope().ifPresent(noticeScope -> {
            NoticeFieldItem noticeFieldItem = new NoticeFieldItem(fieldName, noticeScope.target(), returnType, noticeAnn.isNotField());
            noticeScopeDef(noticeScope, noticeFieldItem);
        });
    }

    public static class ReturnTypeNoticeDef {

        private final Set<NoticeFieldItem> noticeVarDefSet = new InSet<>();

        private final Set<NoticeFieldItem> noticeStaDefSet = new InSet<>();

        private List<NodeField> nodeFields;

        private NoticeFieldItem storyResultDef;

        public List<NodeField> getNodeFields() {
            return nodeFields;
        }

        public void setNodeFields(List<NodeField> nodeFields) {
            this.nodeFields = nodeFields;
        }

        public Set<NoticeFieldItem> getNoticeVarDefSet() {
            return noticeVarDefSet;
        }

        public Set<NoticeFieldItem> getNoticeStaDefSet() {
            return noticeStaDefSet;
        }

        public NoticeFieldItem getStoryResultDef() {
            return storyResultDef;
        }
    }

    public static class InSet<T> extends HashSet<T> {

        @Override
        public boolean add(T t) {
            boolean addResult = super.add(t);
            if (!addResult && (t instanceof NoticeFieldItem)) {
                LOGGER.warn("Fields in TaskService results are repeatedly defined! name: {}", ((NoticeFieldItem) t).getTargetName());
            }
            return addResult;
        }
    }

    public static class NoticeFieldItem extends BasicIdentity {

        /**
         * 字段名称
         */
        private final String fieldName;

        /**
         * 实际字段保存的名称
         */
        private final String targetName;

        /**
         * 字段的类型
         */
        private final Class<?> fieldClass;

        /**
         * 是否为结果本身
         */
        private final boolean resultSelf;

        public NoticeFieldItem(String fieldName, String targetName, Class<?> fieldClass, boolean resultSelf) {
            super(Optional.ofNullable(targetName).filter(StringUtils::isNotBlank).orElse(fieldName), IdentityTypeEnum.NOTICE_FIELD);
            this.fieldName = fieldName;
            this.targetName = this.getIdentityId();
            this.fieldClass = fieldClass;
            this.resultSelf = resultSelf;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getTargetName() {
            return targetName;
        }

        public Class<?> getFieldClass() {
            return fieldClass;
        }

        public boolean isResultSelf() {
            return resultSelf;
        }
    }

    /**
     * @author dds
     */
    public static class TaskFieldProperty {

        private final String name;

        private final ScopeTypeEnum scopeTypeEnum;

        private boolean injectSelf;

        public TaskFieldProperty(String name, ScopeTypeEnum scopeTypeEnum) {
            this.name = name;
            this.scopeTypeEnum = scopeTypeEnum;
        }

        public String getName() {
            return name;
        }

        public ScopeTypeEnum getScopeDataEnum() {
            return scopeTypeEnum;
        }

        public boolean isInjectSelf() {
            return injectSelf;
        }

        public void setInjectSelf(boolean injectSelf) {
            this.injectSelf = injectSelf;
        }
    }
}
