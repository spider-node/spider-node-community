package cn.spider.framework.flow.load.proxy;
import cn.spider.framework.annotation.TaskService;
import cn.spider.framework.linker.sdk.data.*;
import cn.spider.framework.linker.sdk.interfaces.LinkerService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.plugin.proxy
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-14  16:50
 * @Description: 动态代理类
 * @Version: 1.0
 */
public class TaskServiceProxy<T> implements InvocationHandler, Serializable {
    /**
     * 跟服务段交互service
     */
    private LinkerService linkerService;
    /**
     * 工作服务名称
     */
    private String workerName;
    /**
     * 组件名称
     */
    private String componentName;

    public TaskServiceProxy(LinkerService linkerService, String workerName, String componentName) {
        this.linkerService = linkerService;
        this.workerName = workerName;
        this.componentName = componentName;
    }

    /**
     * desc 代理请-linkerServer
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Promise<Object> promise = Promise.promise();
        // 获取事务信息
        LinkerServerRequest linkerServerRequest = buildRequestEntity(args, method);
        JsonObject request = JsonObject.mapFrom(linkerServerRequest);
        Future<JsonObject> result = linkerService.submittals(request);
        result.onSuccess(suss -> {
            LinkerServerResponse linkerServerResponse = suss.mapTo(LinkerServerResponse.class);
            // 校验返回的code
            if (linkerServerResponse.getResultCode().equals(ResultCode.SUSS)) {
                promise.complete(linkerServerResponse.getResultData());
            } else {
                promise.fail(new Exception(linkerServerResponse.getExceptional()));
            }
        }).onFailure(fail -> {
            promise.fail(fail);
        });

        // 返回空对象- 可以改造使用对象池
        return method.getReturnType().newInstance();
    }

    private LinkerServerRequest buildRequestEntity(Object[] args, Method method) {
        // 参数中，移除末尾的 Promise<Object> promise
        LinkerServerRequest linkerServerRequest = new LinkerServerRequest();
        FunctionRequest functionRequest = new FunctionRequest();
        functionRequest.setComponentName(this.componentName);
        functionRequest.setMethodName(method.getName());
        TaskService annotation = method.getAnnotation(TaskService.class);
        functionRequest.setServiceName(annotation.name());
        functionRequest.setWorkerName(this.workerName);
        JsonArray array = new JsonArray();
        linkerServerRequest.setExecutionType(ExecutionType.FUNCTION);
        linkerServerRequest.setFunctionRequest(functionRequest);
        return linkerServerRequest;
    }
}
