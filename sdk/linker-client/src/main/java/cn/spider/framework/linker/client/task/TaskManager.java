package cn.spider.framework.linker.client.task;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.linker.sdk.data.LinkerServerRequest;
import cn.spider.framework.linker.sdk.data.LinkerServerResponse;
import cn.spider.framework.linker.sdk.data.ResultCode;
import cn.spider.framework.linker.sdk.data.TransactionalType;
import cn.spider.framework.proto.grpc.TransferResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.vertxrpc
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-16  14:43
 * @Description: 任务管理
 * @Version: 1.0
 */
@Slf4j
public class TaskManager {

    private ApplicationContext applicationContext;

    private Executor taskPool;

    private Map<String, Method> methodMap;

    private final String TRANSACTION_MANAGER = "spiderTransactionManager";
    private PlatformTransactionManager platformTransactionManager;

    private TransactionDefinition transactionDefinition;

    public TaskManager(ApplicationContext applicationContext,
                       Executor taskPool,
                       PlatformTransactionManager platformTransactionManager,
                       TransactionDefinition transactionDefinition){
        this.applicationContext = applicationContext;
        this.taskPool = taskPool;
        this.platformTransactionManager = platformTransactionManager;
        this.transactionDefinition = transactionDefinition;
        this.methodMap = new ConcurrentHashMap();
    }

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();


    public void runVertxRpc(LinkerServerRequest request, Promise<JsonObject> transferResponsePromise){
        taskPool.execute(() -> {
            // 返回的具体对象给
            Object resultObject = null;
            LinkerServerResponse response = null;
            try {
                // 判断执行的类型
                switch (request.getExecutionType()) {
                    case FUNCTION:
                        // 功能执行
                        resultObject = runFunction(request);
                        break;
                    case TRANSACTION:
                        //事务执行
                        resultObject = runTransaction(request);
                        break;
                }
                response = new LinkerServerResponse();
                response.setResultCode(ResultCode.SUSS);
                response.setResultData(JSONObject.parseObject(JSON.toJSONString(resultObject)));
            } catch (Exception e) {
                // 异常信息给到返回值当中
                response = new LinkerServerResponse();
                response.setResultCode(ResultCode.FAIL);
                response.setExceptional(ExceptionMessage.getStackTrace(e));
            }
            // 返回-spider-server
            transferResponsePromise.complete(JsonObject.mapFrom(response));
        });
    }

    /**
     * Grpc的执行方法
     * @param request
     * @return
     */
    public void runGrpc(LinkerServerRequest request,Promise<TransferResponse> transferResponsePromise){
        taskPool.execute(() -> {
            // 返回的具体对象给
            Object resultObject = null;
            // 返回消息
            String message = "";
            TransferResponse response = null;
            try {
                // 判断执行的类型
                switch (request.getExecutionType()) {
                    case FUNCTION:
                        // 功能执行
                        resultObject = runFunction(request);
                        break;
                    case TRANSACTION:
                        //事务执行
                        resultObject = runTransaction(request);
                        break;
                }
                message = "执行成功";
                response = TransferResponse.newBuilder()
                        .setCode(1001)
                        .setMessage(message)
                        .setData(Objects.nonNull(resultObject) ? JSON.toJSONString(resultObject) : "{}")
                        .build();
            } catch (Exception e) {
                // 异常信息给到返回值当中
                message = ExceptionMessage.getStackTrace(e);
                response = TransferResponse.newBuilder()
                        .setCode(1002)
                        .setMessage(message)
                        .build();
            }
            // 返回-spider-server
            transferResponsePromise.complete(response);
        });
    }

    /**
     * 功能执行
     *
     * @param request
     * @return object
     */
    public Object runFunction(LinkerServerRequest request) {

        Object target = applicationContext.getBean(request.getFunctionRequest().getComponentName());

        // 获取参数
        Map<String, Object> paramMap = request.getFunctionRequest().getParam();
        String methodKey = request.getFunctionRequest().getComponentName() + request.getFunctionRequest().getMethodName();
        Method methodNew = null;
        if (methodMap.containsKey(methodKey)) {
            methodNew = methodMap.get(methodKey);
        } else {
            // 方法
            Method[] methods = target.getClass().getMethods();
            // 获取要执行的方法
            for (Method method : methods) {
                if (StringUtils.equals(method.getName(), request.getFunctionRequest().getMethodName())) {
                    methodNew = method;
                    break;
                }
            }
            // 方法缓存
            methodMap.put(methodKey, methodNew);
        }
        // 获取执行参数
        Object[] params = buildParam(paramMap, methodNew);
        // 获取事务的xid
        String xid = request.getFunctionRequest().getXid();
        // 获取事务的 branchId
        String branchId = request.getFunctionRequest().getBranchId();
        /**
         * 当需要事务的情况下，使用编程事务
         */
        if (StringUtils.isNotEmpty(xid) && StringUtils.isNotEmpty(branchId)) {
            TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);
            try {
                Object result = ReflectionUtils.invokeMethod(methodNew, target, params);
                platformTransactionManager.commit(transaction);
                return result;
            } catch (Exception e) {
                platformTransactionManager.rollback(transaction);
                throw new RuntimeException(e);
            }
        }
        log.warn("执行得参数为 {}",JSON.toJSONString(params));
        // 具体执行
        return ReflectionUtils.invokeMethod(methodNew, target, params);

    }

    /**
     * 事务操作
     *
     * @param request
     * @return object
     * @throws NoSuchMethodException
     */
    public Object runTransaction(LinkerServerRequest request) {
        Object target = applicationContext.getBean(TRANSACTION_MANAGER);
        Method method = null;

        String methodName = request.getTransactionalRequest().getTransactionalType().equals(TransactionalType.ROLLBACK) ? "rollBack" : "commit";
        if (methodMap.containsKey(methodName)) {
            method = methodMap.get(methodName);
        } else {
            Method[] methods = target.getClass().getMethods();

            for (Method methodNew : methods) {
                if (StringUtils.equals(methodNew.getName(), methodName)) {
                    method = methodNew;
                    break;
                }
            }
            methodMap.put(methodName, method);
        }
        String transactionId = request.getTransactionalRequest().getTransactionId();
        String brushId = request.getTransactionalRequest().getBranchId();
        Object[] params = new Object[2];
        params[0] = transactionId;
        params[1] = brushId;
        return ReflectionUtils.invokeMethod(method, target, params);
    }

    public Object[] buildParam(Map<String, Object> paramMap, Method method) {

        Parameter[] parameters = method.getParameters();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Object[] params = new Object[parameterNames.length];
        if(parameterNames.length == 1){
            Class paramType = parameters[0].getType();
            params[0] = JSON.parseObject(JSON.toJSONString(paramMap), paramType);
            return params;
        }
        for (int i = 0; i < parameterNames.length; i++) {
            Parameter parameter = parameters[i];
            String parameterName = parameterNames[i];
            if(!paramMap.containsKey(parameterName)){
                params[i] = null;
                continue;
            }
            Object requestParam = paramMap.get(parameterName);
            if(Objects.isNull(requestParam)){
                params[i] = null;
                continue;
            }
            Class paramType = parameter.getType();
            params[i] = JSON.parseObject(JSON.toJSONString(requestParam), paramType);
        }
        return params;
    }


}
