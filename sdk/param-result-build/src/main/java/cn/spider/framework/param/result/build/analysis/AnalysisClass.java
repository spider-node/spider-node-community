package cn.spider.framework.param.result.build.analysis;

import cn.spider.framework.annotation.TaskComponent;
import cn.spider.framework.annotation.TaskInstruct;
import cn.spider.framework.annotation.TaskService;;
import cn.spider.framework.param.result.build.*;
import cn.spider.framework.param.result.build.scan.loader.AnalysisClassLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.vertx.core.json.JsonObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 解析动态价值的url
 */
public class AnalysisClass {

    public Object analysis(String taskComponent, String taskService, String classPath, String url) throws MalformedURLException {
        Map<String, Map<String, Object>> all = buildParam(url, classPath);
        if (StringUtils.isEmpty(taskComponent) && StringUtils.isEmpty(taskService)) {
            return all;
        }
        return all.get(taskComponent + taskService);
    }

    /**
     * 获取jar中的出参,入参
     * @param url 请求地址
     * @param classPath 扫码的class地址
     * @return 返回出参,入参配置
     * @throws MalformedURLException url异常
     */
    public Map<String, Map<String, Object>> buildParam(String url, String classPath) throws MalformedURLException {
        URL jar = new URL("jar:" + url + "!/");
        // step2: 创建 appointClassLoader
        AnalysisClassLoader appointClassLoader = new AnalysisClassLoader(jar, AnalysisClass.class.getClassLoader());
        // step3: 进行加载替换
        Set<Class> classes = appointClassLoader.loadClassNew(classPath);
        Map<String, Map<String, Object>> paramAll = new HashMap<>();
        for (Class clazz : classes) {
            if (!clazz.isInterface()) {
                continue;
            }
            Map<String, Map<String, Object>> param = doInit(clazz, true);
            paramAll.putAll(param);
        }
        return paramAll;
    }

    protected Map<String, Map<String, Object>> doInit(Class<?> targetClass, boolean scanSuper) {
        TaskComponent taskComponent = targetClass.getAnnotation(TaskComponent.class);
        Method[] taskServiceMethods = MethodUtils.getMethodsWithAnnotation(targetClass, TaskService.class, false, false);
        List<Method> taskServiceMethodList = filterTaskServiceMethods(taskServiceMethods, targetClass, scanSuper);
        if (CollectionUtils.isEmpty(taskServiceMethodList)) {
            return null;
        }
        Map<String, Map<String, Object>> allMapping = new HashMap<>();
        taskServiceMethodList.forEach(method -> {
            Map<String, Object> mapping = new HashMap<>();
            TaskService annotation = method.getAnnotation(TaskService.class);
            String taskServiceName = StringUtils.isBlank(annotation.name()) ? method.getName() : annotation.name();
            TaskInstructWrapper taskInstruct = getTaskInstructWrapper(method, taskServiceName).orElse(null);
            NoticeAnnotationWrapper noticeMethodSpecify = new NoticeAnnotationWrapper(method);
            MethodWrapper methodWrapper = new MethodWrapper(method, annotation, noticeMethodSpecify, taskInstruct, true);
            List<ParamInjectDef> paramInjectDefsList = new ArrayList<>();
            methodWrapper.getReturnTypeNoticeDef().getNoticeStaDefSet().stream().forEach(item -> {
                String targetName = item.getTargetName();
                Field[] fields = item.getFieldClass().getDeclaredFields();
                if (fields.length > 0) {
                    List<ParamInjectDef> paramInjectDefs = new ArrayList<>();
                    for (Field field : fields) {
                        ParamInjectDef parameter = new ParamInjectDef(field.getName(), targetName + "." + field.getName());
                        paramInjectDefs.add(parameter);
                    }
                    paramInjectDefsList.addAll(paramInjectDefs);
                }
            });
            Object params = CollectionUtils.isEmpty(methodWrapper.getParamInjectDefs()) ? null : methodWrapper.getParamInjectDefs().get(0).getFieldInjectDefList();
            // 构造入参
            JsonObject paramObject = new JsonObject();
            paramObject.put("nodeParamConfigs", params);
            // 构造出参
            JsonObject resultObject = new JsonObject();
            resultObject.put("nodeParamConfigs", paramInjectDefsList);

            mapping.put("param", paramObject);
            mapping.put("result", resultObject);
            mapping.put("worker",taskComponent.workerName());
            mapping.put("method",methodWrapper.getMethod().getName());
            // 改造获取入参,请求参数
            allMapping.put(taskComponent.name() + "@" + taskServiceName, mapping);
        });
        return allMapping;
    }


    private List<Method> filterTaskServiceMethods(Method[] taskServiceMethods, Class<?> targetClass, boolean scanSuper) {
        if (ArrayUtils.isEmpty(taskServiceMethods)) {
            return Lists.newArrayList();
        }
        List<Method> taskServiceMethodList = Arrays.stream(taskServiceMethods).filter(tsm -> {
            if (scanSuper) {
                return true;
            }
            return targetClass.isAssignableFrom(tsm.getDeclaringClass());
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskServiceMethodList)) {
            return Lists.newArrayList();
        }

        List<Method> methodList = Lists.newArrayList();
        taskServiceMethodList.stream().collect(Collectors.groupingBy(m -> {
            TaskService annotation = m.getAnnotation(TaskService.class);
            String name = StringUtils.isBlank(annotation.name()) ? StringUtils.uncapitalize(m.getName()) : annotation.name();
            return TaskServiceUtil.joinName(name, annotation.ability());
        })).forEach((ts, list) -> {
            if (list.size() <= 1) {
                methodList.add(list.get(0));
                return;
            }

            list.sort((m1, m2) -> {
                if (!m1.getReturnType().isAssignableFrom(m2.getReturnType())) {
                    return -1;
                }
                Class<?>[] p1List = m1.getParameterTypes();
                Class<?>[] p2List = m2.getParameterTypes();
                if (p1List.length == 0) {
                    return 0;
                }
                for (int i = 0; i < p1List.length; i++) {
                    if (!p1List[i].isAssignableFrom(p2List[i])) {
                        return -1;
                    }
                }
                return 0;
            });

            methodList.add(list.get(0));
        });
        return methodList;
    }

    private Optional<TaskInstructWrapper> getTaskInstructWrapper(Method method, String taskService) {
        TaskInstruct annotation = method.getAnnotation(TaskInstruct.class);
        if (annotation == null) {
            return Optional.empty();
        }
        return Optional.of(new TaskInstructWrapper(annotation, taskService));
    }
}
