package cn.spider.framework.flow.load.proxy.factory;

import cn.spider.framework.flow.load.proxy.TaskServiceProxy;

import java.lang.reflect.Proxy;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.plugin.factory
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-14  16:45
 * @Description: 代理类的工厂，为接口生成实现类
 * @Version: 1.0
 */
public class TaskServiceProxyFactory<T>  {
    private final Class<T> taskServiceInterface;

    public TaskServiceProxyFactory(Class<T> taskServiceInterface) {
        this.taskServiceInterface = taskServiceInterface;
    }

    @SuppressWarnings("unchecked")
    public T newInstance(TaskServiceProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(taskServiceInterface.getClassLoader(), new Class[] { taskServiceInterface }, mapperProxy);
    }

}
