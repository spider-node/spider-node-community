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

import cn.spider.framework.annotation.enums.ScopeTypeEnum;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.flow.bpmn.FlowElement;
import cn.spider.framework.flow.bpmn.ServiceTask;
import cn.spider.framework.flow.bus.*;
import cn.spider.framework.flow.constant.GlobalProperties;
import cn.spider.framework.flow.container.component.ParamInjectDef;
import cn.spider.framework.flow.container.component.TaskInstructWrapper;
import cn.spider.framework.flow.engine.ParamLifecycle;
import cn.spider.framework.flow.engine.SpringParamLifecycle;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.monitor.MonitorTracking;
import cn.spider.framework.flow.monitor.ParamTracking;
import cn.spider.framework.flow.role.Role;
import cn.spider.framework.param.sdk.data.QueryFunctionParam;
import cn.spider.framework.param.sdk.interfaces.ParamInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.noear.snack.ONode;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * TaskServiceUtil
 *
 * @author dds
 */
@Slf4j
public class TaskServiceUtil {

    /**
     * service name + ability name
     *
     * @param left  service name
     * @param right ability name
     * @return name
     */
    public static String joinName(String left, String right) {
        return innerJoin(left, right, "@");
    }

    public static String joinVersion(String left, long version) {
        return innerJoin(left, String.valueOf(version), "-");
    }

    private static String innerJoin(String left, String right, String sign) {
        AssertUtil.notBlank(left);
        if (StringUtils.isBlank(right)) {
            return left;
        }
        return left + sign + right;
    }


    /**
     * 获取功能执行-返回的参数
     * @param resultMapping
     * @param paramInterface
     * @param requestId
     * @return
     */
    public static Future<Map<String, Object>> getResultObject(Map<String, String> resultMapping, ParamInterface paramInterface,String requestId) {
        Map<String, Object> resultObject = Maps.newHashMap();
        if (resultMapping.isEmpty()) {
            return Future.succeededFuture(resultObject);
        }
        Promise<Map<String, Object>> promise = Promise.promise();
        QueryFunctionParam queryFunctionParam = new QueryFunctionParam(resultMapping,requestId);
        paramInterface.queryFunctionResult(JsonObject.mapFrom(queryFunctionParam)).onSuccess(suss->{
            promise.complete(suss.getMap());
        }).onFailure(fail->{
            log.error("获取返回参数失败-{}", ExceptionMessage.getStackTrace(fail));
            promise.complete(new HashMap<>());
        });
        return promise.future();
    }

    /**
     * 获取目标方法入参 -- 改造成异步
     */
    public static Map<String, Object> getTaskParams(boolean isCustomRole, boolean tracking, ServiceTask serviceTask, StoryBus storyBus, Role role, TaskInstructWrapper taskInstructWrapper,
                                                    List<ParamInjectDef> paramInjectDefs, Function<ParamInjectDef, Object> paramInitStrategy, ApplicationContext applicationContext) {

        if (CollectionUtils.isEmpty(paramInjectDefs)) {
            return new HashMap<>();
        }
        AssertUtil.notNull(serviceTask);
        //Long starts = System.currentTimeMillis();
        //  log.info("获取参数-------------start {} 时间 {}",serviceTask.getTaskService(),starts);
        Optional<MonitorTracking> trackingOptional = Optional.of(storyBus.getMonitorTracking()).filter(t -> tracking);
        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < paramInjectDefs.size(); i++) {

            // 没有参数定义时，取默认值
            ParamInjectDef iDef = paramInjectDefs.get(i);
            if (iDef == null) {
                continue;
            }

            // 如果是基本数据类型，进行基本数据类型初始化
            boolean isPrimitive = iDef.getParamType().isPrimitive();
            if (isPrimitive) {
                Object object = ElementParserUtil.initPrimitive(iDef.getParamType());
                paramMap.put(iDef.getFieldName(), object);
            }
            if (iDef.notNeedInject()) {
                continue;
            }

            // 如果拿入参的 request 参数，直接赋值
            if (iDef.getScopeDataEnum() == ScopeTypeEnum.REQUEST && iDef.isInjectSelf()) {
                // 转换
                trackingOptional.ifPresent(mt -> mt.trackingNodeParams(serviceTask, () ->
                        ParamTracking.build(iDef.getFieldName(), storyBus.getReq(), ScopeTypeEnum.REQUEST, ScopeTypeEnum.REQUEST.name().toLowerCase())));
                paramMap.put(iDef.getFieldName(), storyBus.getReq());
                continue;
            }

            if (taskInstructWrapper != null && StringUtils.isNotBlank(serviceTask.getTaskInstruct()) && InstructContent.class.isAssignableFrom(iDef.getParamType())) {
                InstructContent instructContent = new InstructContent(serviceTask.getTaskInstruct(), serviceTask.getTaskInstructContent());
                trackingOptional.ifPresent(mt -> mt.trackingNodeParams(serviceTask, () -> ParamTracking.build(iDef.getFieldName(), null, ScopeTypeEnum.EMPTY, "instruct")));
                paramMap.put(iDef.getFieldName(), instructContent);
            }

            // 如果目标类是 CustomRole 且方法入参需要 Role 时，直接透传 role
            if (isCustomRole && Role.class.isAssignableFrom(iDef.getParamType())) {
                trackingOptional.ifPresent(mt -> mt.trackingNodeParams(serviceTask, () -> ParamTracking.build(iDef.getFieldName(), null, ScopeTypeEnum.EMPTY, "role")));
                paramMap.put(iDef.getFieldName(), role);
                continue;
            }

            // 入参是 ScopeDataOperator 时，注入ScopeDataOperator
            if (ScopeDataQuery.class.isAssignableFrom(iDef.getParamType())) {
                trackingOptional.ifPresent(mt -> mt.trackingNodeParams(serviceTask, () -> ParamTracking.build(iDef.getFieldName(), null, ScopeTypeEnum.EMPTY, "dataOperator")));
                paramMap.put(iDef.getFieldName(), storyBus.getScopeDataOperator());
                continue;
            }

            // 参数被 @TaskParam、@ReqTaskParam、@VarTaskParam、@StaTaskParam 注解修饰时，从 StoryBus 中直接获取变量并赋值给参数
            if (iDef.getScopeDataEnum() != null && StringUtils.isNotBlank(iDef.getTargetName())) {
                String targetName = serviceTask.queryConfigFieldName(iDef.getTargetName());

                ScopeTypeEnum scopeTypeEnum = iDef.getScopeDataEnum();
                if (targetName.startsWith("req.")) {
                    targetName = targetName.substring(4);
                    scopeTypeEnum = ScopeTypeEnum.REQUEST;
                }
                Object rs = storyBus.getValueNode(scopeTypeEnum, targetName).orElse(null);
                String targetNameNew = targetName;
                if (rs == PropertyUtil.GET_PROPERTY_ERROR_SIGN) {
                    trackingOptional.ifPresent(mt -> mt.trackingNodeParams(serviceTask, () ->
                            ParamTracking.build(iDef.getFieldName(), MonitorTracking.BAD_VALUE, iDef.getScopeDataEnum(), targetNameNew)));
                    continue;
                }
                if (isPrimitive && rs == null) {
                    Object primitiveFinalObj = paramMap.get(iDef.getFieldName());
                    trackingOptional.ifPresent(mt -> mt.trackingNodeParams(serviceTask, () ->
                            ParamTracking.build(iDef.getFieldName(), primitiveFinalObj, iDef.getScopeDataEnum(), targetNameNew)));
                    continue;
                }
                Object r = null;
                if (rs instanceof ONode) {
                    ONode node = (ONode) r;
                    r = node.toObject(iDef.getParamType());
                } else {
                    r = rs;
                }
                checkParamType(serviceTask, iDef, r);
                paramMap.put(iDef.getFieldName(), r);
                continue;
            }

            // case 1：参数 Bean 需要解析注入
            // case 2：参数需要 Spring 容器实例化
            // case 3：参数实现 ParamLifecycle 接口
            if (CollectionUtils.isNotEmpty(iDef.getFieldInjectDefList()) || iDef.isSpringInitialization() || ParamLifecycle.class.isAssignableFrom(iDef.getParamType())) {
                Object o = paramInitStrategy.apply(iDef);
                if (o instanceof SpringParamLifecycle) {
                    ((SpringParamLifecycle) o).initContext(applicationContext);
                }
                if (o instanceof ParamLifecycle) {
                    ((ParamLifecycle) o).before(storyBus.getScopeDataOperator());
                }

                List<ParamInjectDef> fieldInjectDefList = iDef.getFieldInjectDefList();
                if (CollectionUtils.isNotEmpty(fieldInjectDefList)) {
                    fieldInjectDefList.forEach(def -> {
                        if (def.notNeedInject()) {
                            return;
                        }
                        String targetName = serviceTask.queryConfigFieldName(def.getTargetName());
                        ScopeTypeEnum scopeTypeEnum = def.getScopeDataEnum();
                        Object value = null;
                        if(targetName.startsWith("spider.")){
                            Map<String,Object> param = serviceTask.obtainAppointParam();
                            targetName = targetName.substring(7);
                            if(!param.containsKey(targetName)){
                                return;
                            }
                            Object spiderValue = param.get(targetName);
                            ONode node = ONode.loadObj(spiderValue);
                            value = node.toObject(def.getParamType());
                        }else {
                            if (targetName.startsWith("req.")) {
                                targetName = targetName.substring(4);
                                scopeTypeEnum = ScopeTypeEnum.REQUEST;
                            }
                            // 处理targetName
                            String targetNames = targetNameHandler(targetName);
                            log.info("获取参数的.targetName {} targetNames {} ", targetName, targetNames);
                            Object values = storyBus.getValueNode(scopeTypeEnum, targetNames).orElse(null);

                            if (values == PropertyUtil.GET_PROPERTY_ERROR_SIGN) {
                                trackingOptional.ifPresent(mt -> mt.trackingNodeParams(serviceTask, () ->
                                        ParamTracking.build(iDef.getFieldName() + "." + def.getFieldName(), MonitorTracking.BAD_VALUE, def.getScopeDataEnum(), def.getTargetName())));
                                return;
                            } else if (values instanceof ONode) {
                                if(Objects.nonNull(values)){
                                    ONode node = buildConvertParam(targetName, (ONode) values);

                                    value = node.toObject(def.getParamType());
                                }
                            } else {
                                value = values;
                            }
                        }
                        checkParamType(serviceTask, def, value);
                        PropertyUtil.setProperty(o, def.getFieldName(), value);
                    });
                }
                paramMap.put(iDef.getFieldName(), o);
            }

        }
        // log.info("获取参数-------------end {} 时间 {}",serviceTask.getId(),System.currentTimeMillis()-starts);
        return paramMap;
    }

    private static String targetNameHandler(String targetName) {
        if (targetName.contains(".convert(")) {
            int indexOfConvert = targetName.indexOf(".convert");
            return targetName.substring(0, indexOfConvert);
        }
        return targetName;
    }

    /**
     * 支持转换不同域中的功能
     *
     * @param targetName
     * @param node
     * @return
     */
    private static ONode buildConvertParam(String targetName, ONode node) {
        if (targetName.contains(".convert(")) {
            int indexOfConvert1 = targetName.indexOf("(");
            String afterConvert1 = targetName.substring(indexOfConvert1 + 1, targetName.length() - 1);
            Map<String, String> paramConvert = new HashMap<>();
            // afterConvert1 根据逗号进行分割成字符串的数组
            String[] split = afterConvert1.split(",");
            for (String mappingKey : split) {
                String[] split2 = mappingKey.split(":");
                paramConvert.put(split2[0], split2[1]);
            }
            // 判断是否为数组
            if (node.isArray()) {
                List<Map> results = node.toObjectList(Map.class);
                List<Map<String, Object>> result = new ArrayList<>(results.size());
                for (Map map : results) {
                    Map<String, Object> convertObject = new HashMap<>();
                    for (Object key : map.keySet()) {
                        if (!paramConvert.containsKey(key)) {
                            continue;
                        }
                        Object object = map.get(key);
                        String convertKey = paramConvert.get(key);
                        convertObject.put(convertKey, object);
                    }
                    if (!convertObject.isEmpty()) {
                        result.add(convertObject);
                    }
                }
                return ONode.load(result);
            }
            // 为单纯对象不为数组
            Map<String, Object> resultMap = node.toObject(Map.class);
            Map<String, Object> convertObject = new HashMap<>();
            for (String key : resultMap.keySet()) {
                if (!paramConvert.containsKey(key)) {
                    continue;
                }
                String convertKey = paramConvert.get(key);
                Object result = resultMap.get(key);
                convertObject.put(convertKey, result);
            }
            if (!convertObject.isEmpty()) {
                return ONode.load(convertObject);
            }
        }
        return node;
    }

    private static void checkParamType(FlowElement flowElement, ParamInjectDef def, Object value) {
        boolean correctType = (value == null) || ElementParserUtil.isAssignable(def.getParamType(), value.getClass());
        AssertUtil.isTrue(correctType, ExceptionEnum.SERVICE_PARAM_ERROR, "The actual type does not match the expected type! nodeName: {}, actual: {}, expected: {}, paramName {}",
                () -> {
                    String actual = (value == null) ? "null" : value.getClass().getName();
                    return Lists.newArrayList(flowElement.getName(), actual, def.getParamType().getName(), def.getFieldName());
                }
        );
    }

    @SuppressWarnings("unchecked")
    public static void fillTaskParams(Map<String, Object> paramMap, Map<String, Object> taskParams,
                                      List<ParamInjectDef> paramInjectDefs, Function<ParamInjectDef, Object> paramInitStrategy, ScopeDataOperator scopeDataOperator) {
        if (!GlobalProperties.SERVICE_NODE_DEFINE_PARAMS) {
            return;
        }
        if (paramMap == null || paramMap.isEmpty() || MapUtils.isEmpty(taskParams) || CollectionUtils.isEmpty(paramInjectDefs)) {
            return;
        }
        for (int i = 0; i < paramInjectDefs.size(); i++) {
            ParamInjectDef iDef = paramInjectDefs.get(i);
            if (iDef == null) {
                continue;
            }
            String fName = StringUtils.isBlank(iDef.getTargetName()) ? iDef.getFieldName() : iDef.getTargetName();

            AssertUtil.notBlank(fName);
            if (!taskParams.containsKey(fName)) {
                continue;
            }
            Object val = taskParams.get(fName);
            if (val == null) {

                Object object = iDef.getParamType().isPrimitive() ? ElementParserUtil.initPrimitive(iDef.getParamType()) : null;
                paramMap.put(iDef.getFieldName(), object);
                continue;
            }
            try {
                if (val instanceof String) {
                    Object object = parseParamValue((String) val, scopeDataOperator, iDef.getParamType());
                    paramMap.put(iDef.getFieldName(), object);
                    continue;
                }
                if (val instanceof Map) {
                    if (!paramMap.containsKey(iDef.getFieldName())) {
                        Object object = paramInitStrategy.apply(iDef);
                        paramMap.put(iDef.getFieldName(), object);
                    }
                    AssertUtil.notNull(paramMap.get(iDef.getFieldName()));
                    Map<String, ?> valMap = (Map<String, ?>) val;
                    if (MapUtils.isEmpty(valMap)) {
                        continue;
                    }

                    Map<String, ParamInjectDef> defMap = Maps.newHashMap();
                    if (CollectionUtils.isNotEmpty(iDef.getFieldInjectDefList())) {
                        iDef.getFieldInjectDefList().forEach(def -> defMap.put(def.getFieldName(), def));
                    }
                    for (Field field : paramMap.get(iDef.getFieldName()).getClass().getDeclaredFields()) {
                        String targetName = Optional.ofNullable(defMap.get(field.getName()))
                                .filter(def -> field.getType() == def.getParamType()).map(ParamInjectDef::getTargetName).orElse(field.getName());
                        if (!valMap.containsKey(targetName)) {
                            continue;
                        }
                        Object v = valMap.get(targetName);
                        if (v == null) {
                            PropertyUtil.setProperty(paramMap.get(iDef.getFieldName()), field.getName(), field.getType().isPrimitive() ? ElementParserUtil.initPrimitive(field.getType()) : null);
                            continue;
                        }
                        Object vObj = parseParamValue(v instanceof String ? (String) v : JSON.toJSONString(v), scopeDataOperator, field.getType());
                        PropertyUtil.setProperty(paramMap.get(iDef.getFieldName()), field.getName(), vObj);
                    }
                    continue;
                }
            } catch (JSONException e) {
                throw ExceptionUtil.buildException(e, ExceptionEnum.TYPE_TRANSFER_ERROR,
                        GlobalUtil.format("External specified parameter type conversion exception. index: {}, paramName: {}, message: {}", i, fName, e.getMessage()));
            }
            AssertUtil.isTrue(false, ExceptionEnum.SERVICE_PARAM_ERROR, "taskParams does not allow invalid value types to appear. taskParams: {}", taskParams);
        }
    }

    private static Object parseParamValue(String valStr, ScopeDataOperator scopeDataOperator, Class<?> type) {
        if (valStr.startsWith("@") && ElementParserUtil.isValidDataExpression(valStr.substring(1))) {
            return scopeDataOperator.getData(valStr.substring(1)).orElse(null);
        }
        return JSON.parseObject(valStr, type);
    }
}
