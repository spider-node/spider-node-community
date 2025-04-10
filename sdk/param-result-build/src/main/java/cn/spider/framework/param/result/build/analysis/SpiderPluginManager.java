package cn.spider.framework.param.result.build.analysis;

import cn.spider.framework.annotation.TaskComponent;
import cn.spider.framework.annotation.TaskInstruct;
import cn.spider.framework.annotation.TaskService;;
import cn.spider.framework.common.utils.PluginKeyUtil;
import cn.spider.framework.common.utils.ProxyUtil;
import cn.spider.framework.param.result.build.*;
import cn.spider.framework.param.result.build.model.NodeParamInfo;
import cn.spider.framework.param.result.build.model.NodeParamInfoBath;
import cn.spider.framework.param.result.build.model.SpiderPlugin;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 解析动态价值的url
 */
@Slf4j
public class SpiderPluginManager {

    /**
     * 插件中的方法管理
     */
    private Map<String, SpiderPlugin> methodMap;

    private NodeParamInfoBath nodeParamInfoBath;

    private ApplicationContext applicationContext;

    private String bizName;

    private String pomVersion;

    private String taskId;

    private String bizVersion;

    public SpiderPluginManager(ApplicationContext applicationContext, String bizName, String pomVersion, String taskId, String bizVersion) {
        this.applicationContext = applicationContext;
        this.methodMap = new HashMap<>();
        this.bizName = bizName;
        this.pomVersion = pomVersion;
        this.taskId = taskId;
        this.bizVersion = bizVersion;
    }

    public void buildPlugin() {
        Map<String, Object> beansOfClassAnnotation = applicationContext.getBeansWithAnnotation(TaskComponent.class);
        if (beansOfClassAnnotation.isEmpty()) {
            return;
        }
        List<NodeParamInfo> nodeParamInfos = new ArrayList<>();
        beansOfClassAnnotation.values().forEach(item -> {
            Class<?> targetClass = ProxyUtil.noneProxyClass(item);
            NodeParamInfoBath nodeParamInfoBath = this.doInit(targetClass, true, item);
            nodeParamInfos.addAll(nodeParamInfoBath.getNodeParamInfoList());
        });
        try {
            this.nodeParamInfoBath = new NodeParamInfoBath();
            this.nodeParamInfoBath.setNodeParamInfoList(nodeParamInfos);
            nodeParamInfoBath.setTaskId(this.taskId);
            nodeParamInfoBath.setPluginKey(PluginKeyUtil.buildPluginKey(this.bizName, this.pomVersion));
        } catch (Exception e) {
            log.error("获取参数失败");
        }

    }

    public NodeParamInfoBath doInit(Class<?> targetClass, boolean scanSuper, Object target) {
        TaskComponent taskComponent = targetClass.getAnnotation(TaskComponent.class);
        Method[] taskServiceMethods = MethodUtils.getMethodsWithAnnotation(targetClass, TaskService.class, false, false);
        List<Method> taskServiceMethodList = filterTaskServiceMethods(taskServiceMethods, targetClass, scanSuper);
        if (CollectionUtils.isEmpty(taskServiceMethodList)) {
            return null;
        }
        List<NodeParamInfo> nodeParamInfos = new ArrayList<>();
        taskServiceMethodList.forEach(method -> {
            TaskService annotation = method.getAnnotation(TaskService.class);
            String taskServiceName = StringUtils.isBlank(annotation.name()) ? method.getName() : annotation.name();
            NodeParamInfo nodeParamInfo = new NodeParamInfo();
            nodeParamInfo.setTaskComponent(taskComponent.name());
            nodeParamInfo.setTaskService(taskServiceName);
            nodeParamInfo.setVersion(this.bizVersion);
            nodeParamInfo.setTaskId(this.taskId);
            nodeParamInfos.add(nodeParamInfo);
            String key = nodeParamInfo.getTaskComponent() + "@" + nodeParamInfo.getTaskService() + "@" + this.bizVersion;
            SpiderPlugin spiderPlugin = new SpiderPlugin(method, target, key, nodeParamInfo.getTaskComponent(), nodeParamInfo.getTaskService(), nodeParamInfo.getMethod());
            methodMap.put(spiderPlugin.getKey(), spiderPlugin);
        });
        NodeParamInfoBath nodeParamInfoBath = new NodeParamInfoBath();
        nodeParamInfoBath.setNodeParamInfoList(nodeParamInfos);
        return nodeParamInfoBath;
    }

    private List<Method> filterTaskServiceMethods(Method[] taskServiceMethods, Class<?> targetClass,
                                                  boolean scanSuper) {
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

    public SpiderPlugin get(String key) {
        return methodMap.get(key);
    }

    public List<SpiderPlugin> allPlugin() {
        return new ArrayList<>(methodMap.values());
    }

    public NodeParamInfoBath getNodeParamInfoBath() {
        return nodeParamInfoBath;
    }


}
