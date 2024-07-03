package cn.spider.framework.spider.param.manager;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.domain.sdk.data.NodeParamConfig;
import cn.spider.framework.domain.sdk.data.NodeParamConfigModel;
import cn.spider.framework.domain.sdk.data.QueryBaseNodeParam;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.spider.param.ParamVerticle;
import cn.spider.framework.spider.param.data.NodeParamMapping;
import cn.spider.framework.spider.param.example.ParamExample;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.noear.snack.ONode;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 参数管理，获取参数，构造参数，删除参数
 */
public class ParamExampleManager {

    // 作用与去获取对应的参数实例，当执行完后，会进行移除
    private Map<String, ParamExample> paramExampleMap;

    //prefix
    private final String SPIDER_PREFIX = "spider.";

    private final String REQ_PREFIX = "req.";
    // default
    private final ONode DEFAULT_NODE = ONode.load("{}");

    private final String DEFAULT_PARAMS_KEY = "{}";

    private final String REQUEST_ARAM = "req";

    private NodeInterface nodeInterface;

    public ParamExampleManager(NodeInterface nodeInterface) {
        this.nodeInterface = nodeInterface;
        this.paramExampleMap = new HashMap<>();
    }

    private final static Cache<String, NodeParamMapping> cache = CacheBuilder.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
            .concurrencyLevel(2)
            //设置cache中的数据在写入之后的存活时间为10分钟
            .expireAfterWrite(30, TimeUnit.SECONDS)
            //构建cache实例
            .build();


    public Future<JsonObject> get(String taskComponent, String taskService, String requestId, Map<String, String> paramsMapping, Map<String, Object> appointParam,Map<String, Object> conversionParam) {
        Promise<JsonObject> promise = Promise.promise();
        // 基于taskComponent+taskService 获取到参数列表
        queryNodeParamMapping(taskComponent, taskService)
                .onSuccess(suss -> {
                    NodeParamMapping nodeParamMapping = suss;
                    NodeParamConfigModel nodeParamConfigList = JSON.parseObject(nodeParamMapping.getParamMapping().toString(), NodeParamConfigModel.class);
                    Map<String, String> paramCache = new HashMap<>();
                    JsonObject result = new JsonObject();
                    JsonObject param = new JsonObject();
                    ParamExample paramExample = buildParamExample(requestId);
                    result.put(Constant.TASK_METHOD, nodeParamMapping.getTaskMethod());
                    result.put(Constant.WORKER_ID, nodeParamMapping.getWorkerId());
                    if (CollectionUtils.isEmpty(nodeParamConfigList.getNodeParamConfigs())) {
                        promise.complete(result);
                        return;
                    }
                    nodeParamConfigList.getNodeParamConfigs().forEach(item -> {
                        NodeParamConfig paramConfig = item;
                        String targetName = paramConfig.getTargetName();
                        if (Objects.nonNull(paramsMapping) && paramsMapping.containsKey(targetName)) {
                            targetName = paramsMapping.get(targetName);
                            // 兼容取req的数据
                        }
                        // 如果是指定 -- 则直接使用指定的值
                        if (targetName.startsWith(SPIDER_PREFIX)) {
                            Map<String, Object> params = appointParam;
                            targetName = targetName.substring(7);
                            // 判断，spider中是否配置过该内容
                            if (!appointParam.containsKey(targetName)) {
                                return;
                            }
                            // 获取到数据，直接塞进param
                            Object spiderValue = params.get(targetName);
                            param.put(paramConfig.getFieldName(), spiderValue);
                            return;
                        }
                        // 获取第一级的参数
                        String targetNameFirst = queryFirstName(targetName);
                        String jsonParamString = buildParam(paramExample, paramCache, targetNameFirst);
                        // 如果为空
                        if (StringUtils.isEmpty(jsonParamString)) {
                            return;
                        }
                        String propertyName = targetName;
                        String targetNames = targetNameHandler(propertyName);
                        // 获取到正确的值
                        Optional<ONode> node = getPropertyNode(jsonParamString, targetNames);
                        ONode nodes = node.orElse(DEFAULT_NODE);
                        // 判断是否为默认值
                        if (nodes.toString().equals(DEFAULT_PARAMS_KEY)) {
                            // 直接结束
                            return;
                        }
                        ONode oNode = buildConvertParam(propertyName, nodes);

                        Object value = oNode.toObject();

                        if(Objects.nonNull(conversionParam) && !conversionParam.isEmpty() && Objects.nonNull(value) && conversionParam.containsKey(value.toString())){
                            value = conversionParam.get(value.toString());
                        }
                        param.put(paramConfig.getFieldName(), value);
                    });
                    // 设置返回的值
                    result.put(Constant.RUN_PARAM, param);
                    promise.complete(result);
                    // 归还对象
                }).onFailure(fail -> {
                    promise.fail(fail);
                });

        // 转化成需要的参数
        return promise.future();
    }


    /**
     * 根据表达式获取目标值
     *
     * @param targetNameList
     * @param requestId
     * @return
     */
    public ONode queryValueByExpression(String targetNameList, String requestId) throws Exception {
        ParamExample paramExample = buildParamExample(requestId);
        try {
            String targetName = targetNameList;
            String targetNameFirst = queryFirstName(targetName);
            Map<String, String> paramCache = new HashMap<>();
            String jsonParamString = buildParam(paramExample, paramCache, targetNameFirst);
            // 如果为空
            if (StringUtils.isEmpty(jsonParamString)) {
                return null;
            }
            // 获取到正确的值
            String propertyName = targetName;
            String targetNames = targetNameHandler(propertyName);
            Optional<ONode> node = getPropertyNode(jsonParamString, targetNames);
            ONode nodes = node.isPresent() ? node.get() : DEFAULT_NODE;
            // 判断是否为默认值
            if (node.toString().equals(DEFAULT_PARAMS_KEY)) {
                return null;
            }
            return buildConvertParam(propertyName, nodes);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    // 获取相关数据
    public String buildParam(ParamExample paramExample, Map<String, String> paramCache, String domainName) {
        // 防止多次从rocksdb中获取数据-做一个map的缓存
        String jsonParamString;
        if (paramCache.containsKey(domainName)) {
            jsonParamString = paramCache.get(domainName);
        } else {
            jsonParamString = paramExample.get(domainName);
            paramCache.put(domainName, jsonParamString);
        }
        return jsonParamString;
    }

    private ParamExample buildParamExample(String requestId) {
        // 获取到一个参数实例
        RocksdbUtil rocksdbUtil = ParamVerticle.factory.getBean(RocksdbUtil.class);
        ParamExample paramExample = new ParamExample(rocksdbUtil);
        paramExample.init(requestId);
        return paramExample;
    }

    public Optional<ONode> getPropertyNode(String json, String propertyName) {
        try {
            // 获取jsonPath变量中第二个逗号后面的数据
            ONode nodeOne = ONode.loadStr(json);
            ONode resultNode = nodeOne.select(Constant.JSON_PATH_PREFIX + propertyName);
            return Optional.ofNullable(resultNode);
        } catch (Throwable e) {
            return Optional.ofNullable(DEFAULT_NODE);
        }
    }

    private static String targetNameHandler(String targetName) {
        if (targetName.contains(Constant.CONVERT)) {
            int indexOfConvert = targetName.indexOf(Constant.CONVERT_);
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
        if (targetName.contains(Constant.CONVERT)) {
            int indexOfConvert1 = targetName.indexOf(Constant.LEFT_BRACKETS);
            String afterConvert1 = targetName.substring(indexOfConvert1 + 1, targetName.length() - 1);
            Map<String, String> paramConvert = new HashMap<>();
            // afterConvert1 根据逗号进行分割成字符串的数组
            String[] split = afterConvert1.split(Constant.COMMA);
            for (String mappingKey : split) {
                String[] split2 = mappingKey.split(Constant.COLON);
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

    // 查询节点的入参信息
    public Future<NodeParamMapping> queryNodeParamMapping(String taskComponent, String taskService) {
        String key = taskComponent + taskService;
        NodeParamMapping nodeParamMapping = cache.getIfPresent(key);
        if (Objects.nonNull(nodeParamMapping)) {
            return Future.succeededFuture(nodeParamMapping);
        }
        Promise<NodeParamMapping> promise = Promise.promise();
        QueryBaseNodeParam param = new QueryBaseNodeParam();
        param.setTaskComponent(taskComponent);
        param.setTaskService(taskService);
        nodeInterface.queryBaseNodes(JsonObject.mapFrom(param))
                .onSuccess(suss -> {
                    JsonObject node = suss;
                    JsonObject paramMapping = node.getJsonObject(Constant.PARAM_MAPPING);
                    JsonObject resultMapping = node.getJsonObject(Constant.RESULT_MAPPING);
                    NodeParamMapping nodeParamMappings = new NodeParamMapping();
                    nodeParamMappings.setParamMapping(paramMapping);
                    nodeParamMappings.setResultMapping(resultMapping);
                    nodeParamMappings.setTaskMethod(node.getString(Constant.TASK_METHOD));
                    nodeParamMappings.setTaskComponent(taskComponent);
                    nodeParamMappings.setTaskService(taskService);
                    nodeParamMappings.setWorkerId(node.getString(Constant.WORKER_ID));
                    promise.complete(nodeParamMappings);
                    cache.put(key, nodeParamMappings);
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 在参数中新增参数
     */
    public Future<Void> notifyResult(String taskComponent, String taskService, String requestId, JsonObject result) {
        Promise<Void> promise = Promise.promise();
        ParamExample paramExample = buildParamExample(requestId);
        queryNodeParamMapping(taskComponent, taskService)
                .onSuccess(suss -> {
                    NodeParamMapping nodeParamMapping = suss;
                    JsonObject resultMapping = nodeParamMapping.getResultMapping();
                    // 获取参数的值映射
                    NodeParamConfigModel nodeParamConfigList = JSON.parseObject(resultMapping.toString(),NodeParamConfigModel.class);
                    Map<String, String> paramCache = new HashMap<>();
                    nodeParamConfigList.getNodeParamConfigs().forEach(item -> {
                        NodeParamConfig paramConfig = item;
                        Map<String, Object> resultMap = result.getMap();
                        // 当获取到的值为空的情况下，直接不进行设置
                        if (!resultMap.containsKey(paramConfig.getFieldName())) {
                            return;
                        }
                        // 获取到具体的值
                        Object value = resultMap.get(paramConfig.getFieldName());
                        String targetNameFirst = queryFirstName(paramConfig.getTargetName());

                        String jsonParamString = buildParam(paramExample, paramCache, targetNameFirst);
                        //String setName = queryRealParam(paramConfig.getTargetName());
                        String jsonParamStringNew = setUpJson(paramConfig.getTargetName(), value, StringUtils.isEmpty(jsonParamString) ? "{}" : jsonParamString);
                        paramCache.put(targetNameFirst, jsonParamStringNew);
                    });
                    // 通知上游处理完了
                    // 当为不为空的情况下，直接回写到rocksdb
                    if (!paramCache.isEmpty()) {
                        // 进行回写设置
                        paramCache.forEach((key, value) -> {
                            paramExample.insert(key, value);
                        });
                    }
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                });
        return promise.future();
    }

    /**
     * 设置request参数
     *
     * @param requestId
     * @param param
     */
    public void insertRequestParam(String requestId, JsonObject param) {
        ParamExample paramExample = buildParamExample(requestId);
        JsonObject requestParam = new JsonObject().put(Constant.REQ, param);
        paramExample.insert(REQUEST_ARAM, requestParam.toString());
    }

    private String queryFirstName(String propertyName) {
        if (!propertyName.contains(Constant.SPOT)) {
            return propertyName;
        }
        int index = propertyName.indexOf(Constant.SPOT);
        return propertyName.trim().substring(0, index);
    }

    private String queryRealParam(String propertyName) {
        int index = propertyName.indexOf(Constant.SPOT);
        return propertyName.substring(index + 1, propertyName.length());
    }


    public String setUpJson(String targetName, Object value, String jsonStr) {
        JsonObject finalParam = new JsonObject(jsonStr);
        String name = targetName;
        String[] a = name.split("\\.");
        JsonObject[] jsonObjects = new JsonObject[a.length];
        if (a.length >= 1) {
            for (int i = 0; i < a.length; i++) {
                String key = a[i];
                if (i == 0) {
                    // 第一层
                    JsonObject firstNode = finalParam.containsKey(key) ? finalParam.getJsonObject(key) : new JsonObject();
                    jsonObjects[i] = firstNode;
                    finalParam.put(key, firstNode);
                    continue;
                }
                JsonObject node = jsonObjects[i - 1];
                if (i == a.length - 1) {
                    node.put(key, value);
                    continue;
                }
                JsonObject test = node.containsKey(key) ? node.getJsonObject(key) : new JsonObject();
                node.put(key, test);
                jsonObjects[i] = test;
            }
        }
        return finalParam.toString();
    }

}
