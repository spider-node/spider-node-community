package cn.spider.framework.flow.container.component;

import cn.spider.framework.annotation.TaskComponent;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.ProxyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.container.component
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-21  16:13
 * @Description: TaskComponent管理类
 * @Version: 1.0
 */
public class TaskComponentManager extends TaskComponentRepository {

    /**
     * 指定加载 task-server
     * @param classes
     * @param
     */
    public void appointLoaderTaskServer(Class classes){
        Class<?> targetClass = classes;
        TaskComponent taskComponent = AnnotationUtils.findAnnotation(targetClass, TaskComponent.class);
        AssertUtil.notNull(taskComponent);
        String taskComponentName = StringUtils.isBlank(taskComponent.name()) ? StringUtils.uncapitalize(targetClass.getSimpleName()) : taskComponent.name();
        AssertUtil.notBlank(taskComponentName, ExceptionEnum.COMPONENT_ATTRIBUTES_EMPTY, "TaskComponent name cannot be empty! className: {}", targetClass.getName());
        doInit(classes, taskComponentName, taskComponent.scanSuper());
        // 后置处理
        repositoryPostProcessor();
    }

    /**
     * 卸载
     * @param classes
     */
    public void unload(Class classes){
        unLoader(classes);
    }
}
