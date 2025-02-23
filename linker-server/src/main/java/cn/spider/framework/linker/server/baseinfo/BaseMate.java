package cn.spider.framework.linker.server.baseinfo;

import cn.spider.framework.common.utils.TaskKeyUtil;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.param.result.build.model.NodeParamInfo;
import cn.spider.framework.param.result.build.model.NodeParamInfoBath;
import cn.spider.framework.param.result.build.model.ReportParamInfo;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BaseMate {
    /**
     * 版本号的缓存 key为ip + set
     */
    private final Cache<String, Set<String>> ipCache = CacheBuilder.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
            .concurrencyLevel(2)
            //设置cache中的数据在写入之后的存活时间为1分钟
            .expireAfterWrite(1, TimeUnit.MINUTES)
            //构建cache实例
            .build();


    /**
     * 版本号的缓存 key为ip + set
     */
    private final Cache<String, Set<String>> functionKeyCache = CacheBuilder.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
            .concurrencyLevel(2)
            //设置cache中的数据在写入之后的存活时间为1分钟
            .expireAfterWrite(1, TimeUnit.MINUTES)
            //构建cache实例
            .build();

    private final String IP_CNAME = "IP_CNAME";

    private final String FUNCTION_KEY_CNAME = "FUNCTION_KEY_CNAME";

    private RocksdbUtil rocksdbUtil;

    public BaseMate(RocksdbUtil rocksdbUtil) {
        this.rocksdbUtil = rocksdbUtil;
    }

    /**
     * 注册功能信息
     */
    public void escalationAreaInfo(ReportParamInfo refreshAreaParam, String ip, FunctionEscalationType functionEscalationType) {
        if (Objects.isNull(refreshAreaParam) || CollectionUtils.isEmpty(refreshAreaParam.getNodeParamInfoBathList())) {
            return;
        }
        switch (functionEscalationType) {
            case DEPLOY:
                deploy(refreshAreaParam, ip);
                break;
            case UNLOCK:
                offline(refreshAreaParam, ip);
                break;
        }
    }

    /**
     * 当ip下线的时候，需要做ip对应的信息处理
     */
    public void deployOfflineCache(String ip) {

        if (StringUtils.isEmpty(ip)) {
            return;
        }

        try {
            Set<String> functionKeys = ipCache.getIfPresent(ip);

            if (CollectionUtils.isEmpty(functionKeys)) {
                return;
            }

            for (String functionKey : functionKeys) {
                Set<String> ips = functionKeyCache.getIfPresent(functionKey);
                ips.remove(ip);
                functionKeyCache.put(functionKey, ips);
                deleteFunctionInfo(functionKey, ip);
            }
            deployOfflineRocksdb(ip);
            ipCache.put(ip, Sets.newHashSet());
        } catch (Exception e) {
            throw new RuntimeException("deployOffline_error", e);
        }
    }

    /**
     * 告知ip下线，删除缓存中的功能信息,与功能对应的ip，再删除rocksdb中的内容
     */
    public void deployOfflineRocksdb(String ip) {
        try {
            this.rocksdbUtil.delete(IP_CNAME, ip);
        } catch (Exception e) {
            throw new RuntimeException("deployOffline_error", e);
        }
    }

    /**
     * 获取功能对应的ip
     */
    public void deleteFunctionInfo(String functionKey, String ip) {
        try {
            // 查询 functionKey 对应的ip 在rocksdb
            String ips = this.rocksdbUtil.get(FUNCTION_KEY_CNAME, functionKey);
            if (StringUtils.isEmpty(ips)) {
                return;
            }
            Set<String> ipsSet = JSON.parseObject(ips, Set.class);
            ipsSet.remove(ip);
            if (CollectionUtils.isEmpty(ipsSet)) {
                return;
            }
            this.rocksdbUtil.put(FUNCTION_KEY_CNAME, functionKey, JSON.toJSONString(ipsSet));
        } catch (Exception e) {
            throw new RuntimeException("deleteFunctionInfo_error", e);
        }
    }

    /**
     * 部署
     */
    public void deploy(ReportParamInfo refreshAreaParam, String ip) {
        if (Objects.isNull(refreshAreaParam) || CollectionUtils.isEmpty(refreshAreaParam.getNodeParamInfoBathList())) {
            return;
        }

        List<NodeParamInfoBath> areaModels = refreshAreaParam.getNodeParamInfoBathList();
        // areaModels 循环构造functionKey
        for (NodeParamInfoBath areaModel : areaModels) {
            Set<String> functionKeys = areaModel.getNodeParamInfoList()
                    .stream()
                    .map(nodeParamInfo -> TaskKeyUtil.buildTaskKey(nodeParamInfo.getTaskComponent(), nodeParamInfo.getTaskService()))
                    .collect(Collectors.toSet());
            try {
                putIpInfo(ip, functionKeys);
                for (String functionKey : functionKeys) {
                    putFunctionInfo(functionKey, Sets.newHashSet(ip));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }

    /**
     * 下线
     */
    public void offline(ReportParamInfo refreshAreaParam, String ip) {
        if (Objects.isNull(refreshAreaParam) || CollectionUtils.isEmpty(refreshAreaParam.getNodeParamInfoBathList())) {
            return;
        }
        List<NodeParamInfoBath> areaModels = refreshAreaParam.getNodeParamInfoBathList();
        for (NodeParamInfoBath areaModel : areaModels) {
            List<NodeParamInfo> nodeParamInfos = areaModel.getNodeParamInfoList();
            Set<String> functionKeys = nodeParamInfos
                    .stream()
                    .map(nodeParamInfo -> TaskKeyUtil.buildTaskKey(nodeParamInfo.getTaskComponent(), nodeParamInfo.getTaskService()))
                    .collect(Collectors.toSet());
            try {
                removeIpInfo(ip, functionKeys);
                for (String functionKey : functionKeys) {
                    removeFunctionInfo(functionKey, Sets.newHashSet(ip));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void putIpInfo(String ip, Set<String> functionKeys) throws Exception {
        put(IP_CNAME, ip, functionKeys);
        Set<String> valuesSet = ipCache.getIfPresent(ip);
        if (CollectionUtils.isEmpty(valuesSet)) {
            ipCache.put(ip, functionKeys);
        } else {
            valuesSet.addAll(functionKeys);
            ipCache.put(ip, valuesSet);
        }
    }

    private void removeIpInfo(String ip, Set<String> functionKeys) throws Exception {
        remove(IP_CNAME, ip, functionKeys);
        Set<String> valuesSet = ipCache.getIfPresent(ip);
        if (CollectionUtils.isEmpty(valuesSet)) {
            ipCache.put(ip, functionKeys);
        } else {
            valuesSet.removeAll(functionKeys);
            ipCache.put(ip, valuesSet);
        }
    }

    private void putFunctionInfo(String functionKey, Set<String> ips) throws Exception {
        put(FUNCTION_KEY_CNAME, functionKey, ips);
        Set<String> valuesSet = functionKeyCache.getIfPresent(functionKey);
        if (CollectionUtils.isEmpty(valuesSet)) {
            functionKeyCache.put(functionKey, ips);
        } else {
            valuesSet.addAll(ips);
            functionKeyCache.put(functionKey, valuesSet);
        }
    }

    private void removeFunctionInfo(String functionKey, Set<String> ips) throws Exception {
        remove(FUNCTION_KEY_CNAME, functionKey, ips);
        Set<String> valuesSet = functionKeyCache.getIfPresent(functionKey);
        if (CollectionUtils.isEmpty(valuesSet)) {
            functionKeyCache.put(functionKey, ips);
        } else {
            valuesSet.removeAll(ips);
            functionKeyCache.put(functionKey, valuesSet);
        }
    }

    private void put(String cfName, String key, Set<String> functionKeys) throws Exception {
        // rocksdbUtil.put(cfName, key, value);
        String values = getRocksDb(cfName, key);
        Set<String> valuesSet = StringUtils.isEmpty(values) ? new HashSet<>() : JSON.parseObject(values, Set.class);
        valuesSet.addAll(functionKeys);
        rocksdbUtil.put(cfName, key, JSON.toJSONString(valuesSet));
    }

    /**
     * @param cfName
     * @param key
     * @param functionKeys
     * @throws Exception
     */
    private void remove(String cfName, String key, Set<String> functionKeys) throws Exception {
        String values = getRocksDb(cfName, key);
        if (StringUtils.isEmpty(values)) {
            return;
        }
        Set<String> valuesSet = JSON.parseObject(values, Set.class);
        valuesSet.removeAll(functionKeys);
        rocksdbUtil.put(cfName, key, JSON.toJSONString(valuesSet));
    }

    /**
     * 查询rocksdb
     *
     * @param cfName
     * @param key
     * @return
     * @throws Exception
     */
    private String getRocksDb(String cfName, String key) throws Exception {
        return rocksdbUtil.get(cfName, key);
    }

    /**
     * 根据功能key查询ip
     *
     * @param functionKey
     * @return
     * @throws Exception
     */
    public Set<String> queryIpByFunctionKey(String functionKey) throws Exception {
        // 查询缓存，缓存查不到，调用getRocksDb
        Set<String> valuesSet = functionKeyCache.getIfPresent(functionKey);
        if (CollectionUtils.isEmpty(valuesSet)) {
            String values = getRocksDb(FUNCTION_KEY_CNAME, functionKey);
            valuesSet = StringUtils.isEmpty(values) ? new HashSet<>() : JSON.parseObject(values, Set.class);
            functionKeyCache.put(functionKey, valuesSet);
        }
        return valuesSet;
    }

}
