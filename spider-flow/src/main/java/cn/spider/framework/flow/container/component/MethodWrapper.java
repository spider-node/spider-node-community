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
package cn.spider.framework.flow.container.component;
import cn.spider.framework.annotation.*;
import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.flow.bus.InstructContent;
import cn.spider.framework.flow.bus.ScopeDataQuery;
import cn.spider.framework.flow.constant.GlobalConstant;
import cn.spider.framework.flow.engine.ParamLifecycle;
import cn.spider.framework.flow.enums.IdentityTypeEnum;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.resource.identity.BasicIdentity;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ElementParserUtil;
import cn.spider.framework.flow.util.GlobalUtil;
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
import java.util.*;

/**
 * @author lykan
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

    private final InvokeProperties invokeProperties;

    private final boolean isCustomRole;

    private final TaskInstructWrapper taskInstructWrapper;

    public MethodWrapper(@Nonnull Method method, @Nonnull TaskService annotation,
                         @Nonnull NoticeAnnotationWrapper noticeMethodSpecify, TaskInstructWrapper taskInstructWrapper, boolean isCustomRole) {
        AssertUtil.notNull(method);
        AssertUtil.notNull(annotation);
        this.method = method;
        this.kvScope = annotation.kvScope();
        this.noticeMethodSpecify = noticeMethodSpecify;
        this.ability = annotation.ability();
        this.monoResult = false;
        this.isCustomRole = isCustomRole;
        this.taskInstructWrapper = taskInstructWrapper;
        this.invokeProperties = new InvokeProperties(annotation.invoke());
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

    public InvokeProperties getInvokeProperties() {
        return invokeProperties;
    }

    public Optional<TaskInstructWrapper> getTaskInstructWrapper() {
        return Optional.ofNullable(taskInstructWrapper);
    }

    private void methodParser(Method method) {
        Class<?> returnType = method.getReturnType();
        if (!Objects.equals(GlobalConstant.VOID, returnType.getName())) {
            returnTypeParser(method.getGenericReturnType(), returnType);
        }
        Parameter[] parameters = method.getParameters();
        // 获取到对应的接口-方法参数名称
        String[] parameterNames = queryParameterNames(parameters);
        log.info("加载的参数为 {}",JSON.toJSONString(parameterNames));
        if (ArrayUtils.isNotEmpty(parameters)) {
            AssertUtil.isTrue(parameterNames != null && Objects.equals(parameters.length, parameterNames.length), ExceptionEnum.SERVICE_PARAM_ERROR,
                    "The actual parameters of the method do not match the number of parameters resolved by the method entry name. except: {}, actual: {}",
                    parameters.length, (parameterNames == null ? -1 : parameterNames.length));
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
        for(int i = 0;i< parameters.length;i++){
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

            if (Role.class.isAssignableFrom(p.getType()) || ScopeDataQuery.class.isAssignableFrom(p.getType()) || InstructContent.class.isAssignableFrom(p.getType())) {
                injectDefs[i] = new ParamInjectDef(true, p.getType(), parameterNames[i], null);
                continue;
            }

            List<ParamInjectDef> injectDefList = getFieldInjectDefs(p.getType());
            boolean isSpringInitialization = ElementParserUtil.isSpringInitialization(p.getType());
            if (annOptional.isPresent() || CollectionUtils.isNotEmpty(injectDefList)
                    || p.getType().isPrimitive() || isSpringInitialization || ParamLifecycle.class.isAssignableFrom(p.getType())) {
                boolean needInject = !annOptional.isPresent() || GlobalConstant.STORY_DATA_SCOPE.contains(annOptional.get().getScopeDataEnum());
                ParamInjectDef injectDef = new ParamInjectDef(needInject, p.getType(), parameterNames[i], annOptional.orElse(null));
                injectDef.setFieldInjectDefList(injectDefList);
                injectDef.setSpringInitialization(isSpringInitialization);
                injectDefs[i] = injectDef;
            } else {
                ParamInjectDef injectDef = new ParamInjectDef(false, p.getType(), parameterNames[i], null);
                injectDefs[i] = injectDef;
            }
        }
        this.paramInjectDefs = Collections.unmodifiableList(Arrays.asList(injectDefs));
    }

    private List<ParamInjectDef> getFieldInjectDefs(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return null;
        }

        return ElementParserUtil.getFieldInjectDefList(clazz);
    }

    private void returnTypeParser(Type genericReturnType, Class<?> returnType) {
        if (Mono.class.isAssignableFrom(returnType)) {
            returnType = Object.class;
            if (genericReturnType != null) {
                Class<?>[] resolveArray = ResolvableType.forType(genericReturnType).resolveGenerics();
                returnType = (resolveArray.length > 0 && resolveArray[0] != null && !Objects.equals(GlobalConstant.VOID, resolveArray[0].getName())) ? resolveArray[0] : returnType;
            }
            monoResult = true;
        }

        String className = StringUtils.uncapitalize(returnType.getSimpleName());
        parseReturnType(noticeMethodSpecify, returnType, className);

        parseReturnType(new NoticeAnnotationWrapper(returnType), returnType, className);

        List<Field> fieldsList = FieldUtils.getAllFieldsList(returnType);
        if (CollectionUtils.isNotEmpty(fieldsList)) {
            fieldsList.forEach(field -> {
                NoticeAnnotationWrapper fieldNoticeAnn = new NoticeAnnotationWrapper(field);
                parseReturnType(fieldNoticeAnn, field.getType(), field.getName());
            });
        }
    }

    private void noticeScopeDef(NoticeScope annotation, NoticeFieldItem noticeFieldItem) {
        AssertUtil.notNull(annotation);
        if (ArrayUtils.isEmpty(annotation.scope())) {
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
            returnTypeNoticeDef.noticeVarDefSet.add(noticeFieldItem);
            return;
        }

        List<ScopeTypeEnum> scopeTypeList = Lists.newArrayList(annotation.scope());
        if (scopeTypeList.contains(ScopeTypeEnum.STABLE)) {
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
        }
        if (scopeTypeList.contains(ScopeTypeEnum.VARIABLE)) {
            returnTypeNoticeDef.noticeVarDefSet.add(noticeFieldItem);
        }
        if (scopeTypeList.contains(ScopeTypeEnum.RESULT) && returnTypeNoticeDef.storyResultDef == null) {
            returnTypeNoticeDef.storyResultDef = noticeFieldItem;
        }
    }

    private void parseReturnType(NoticeAnnotationWrapper noticeAnn, Class<?> returnType, String fieldName) {

        AssertUtil.notBlank(fieldName);
        AssertUtil.anyNotNull(noticeAnn, returnType);

        noticeAnn.getNoticeSta().ifPresent(noticeSta -> {
            NoticeFieldItem noticeFieldItem = new NoticeFieldItem(fieldName, noticeSta.target(), returnType, noticeAnn.isNotField());
            returnTypeNoticeDef.noticeStaDefSet.add(noticeFieldItem);
        });
        noticeAnn.getNoticeVar().ifPresent(noticeVar -> {
            NoticeFieldItem noticeFieldItem = new NoticeFieldItem(fieldName, noticeVar.target(), returnType, noticeAnn.isNotField());
            returnTypeNoticeDef.noticeVarDefSet.add(noticeFieldItem);
        });
        noticeAnn.getNoticeResult().ifPresent(noticeResult -> {
            if (returnTypeNoticeDef.storyResultDef != null) {
                return;
            }
            returnTypeNoticeDef.storyResultDef = new NoticeFieldItem(fieldName, null, returnType, noticeAnn.isNotField());
        });
        noticeAnn.getNoticeScope().ifPresent(noticeScope -> {
            NoticeFieldItem noticeFieldItem = new NoticeFieldItem(fieldName, noticeScope.target(), returnType, noticeAnn.isNotField());
            noticeScopeDef(noticeScope, noticeFieldItem);
        });
    }

    public static class ReturnTypeNoticeDef {

        private final Set<NoticeFieldItem> noticeVarDefSet = new InSet<>();

        private final Set<NoticeFieldItem> noticeStaDefSet = new InSet<>();

        private NoticeFieldItem storyResultDef;

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
            AssertUtil.notNull(fieldClass);
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
     * @author lykan
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
