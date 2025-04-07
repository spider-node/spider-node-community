package cn.spider.framework.linker.server.baseinfo;

import cn.spider.framework.common.utils.TaskKeyUtil;
import cn.spider.framework.param.result.build.model.NodeParamInfo;
import cn.spider.framework.param.result.build.model.NodeParamInfoBath;
import cn.spider.framework.param.result.build.model.ReportParamInfo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BaseMate {

    // 使用两个Map维护双向关系
    private Map<String, Set<String>> ipToFunctions;
    private Map<String, Set<String>> functionToIps;


    public BaseMate() {
        this.ipToFunctions = new HashMap<>();
        this.functionToIps = new HashMap<>();
    }


    /**
     * 部署
     */
    public void deploy(ReportParamInfo refreshAreaParam, String ip) {
        log.info("上线的内容为 {} ip {}", JSON.toJSONString(refreshAreaParam), ip);
        if (Objects.isNull(refreshAreaParam) || CollectionUtils.isEmpty(refreshAreaParam.getNodeParamInfoBathList())) {
            return;
        }

        List<NodeParamInfoBath> areaModels = refreshAreaParam.getNodeParamInfoBathList();
        // areaModels 循环构造functionKey
        Set<String> functionKeList = new HashSet<>();
        for (NodeParamInfoBath areaModel : areaModels) {
            if (CollectionUtils.isEmpty(areaModel.getNodeParamInfoList())) {
                continue;
            }
            Set<String> functionKeys = areaModel.getNodeParamInfoList()
                    .stream()
                    .map(nodeParamInfo -> TaskKeyUtil.buildComponentKey(nodeParamInfo.getTaskComponent(), nodeParamInfo.getTaskService(), nodeParamInfo.getVersion()))
                    .collect(Collectors.toSet());
            functionKeList.addAll(functionKeys);
        }
        if (CollectionUtils.isEmpty(functionKeList)) {
            return;
        }
        // 把functionKey 添加到ipToFunctions中
        ipToFunctions.computeIfAbsent(ip, k -> new HashSet<>()).addAll(functionKeList);
        for (String functionKey : functionKeList) {
            functionToIps.computeIfAbsent(functionKey, k -> new HashSet<>()).add(ip);
        }
    }

    /**
     * 下线
     */
    public void offline(ReportParamInfo refreshAreaParam, String ip) {
        if (Objects.isNull(refreshAreaParam) || CollectionUtils.isEmpty(refreshAreaParam.getNodeParamInfoBathList())) {
            return;
        }
        log.info("下线的内容为 {} ip {}", JSON.toJSONString(refreshAreaParam), ip);
        List<NodeParamInfoBath> areaModels = refreshAreaParam.getNodeParamInfoBathList();
        Set<String> functionKeList = new HashSet<>();
        for (NodeParamInfoBath areaModel : areaModels) {
            List<NodeParamInfo> nodeParamInfos = areaModel.getNodeParamInfoList();
            if(CollectionUtils.isEmpty(nodeParamInfos)){
                continue;
            }
            Set<String> functionKeys = nodeParamInfos
                    .stream()
                    .map(nodeParamInfo -> TaskKeyUtil.buildTaskKey(nodeParamInfo.getTaskComponent(), nodeParamInfo.getTaskService()))
                    .collect(Collectors.toSet());

            functionKeList.addAll(functionKeys);
        }
        if(CollectionUtils.isEmpty(functionKeList)){
            return;
        }
        // 移除函数和IP的对应关系
        for (String functionKey : functionKeList) {
            Set<String> ips = functionToIps.get(functionKey);
            if (CollectionUtils.isNotEmpty(ips)) {
                ips.remove(ip);
                if (ips.isEmpty()) {
                    functionToIps.remove(functionKey);
                }
            }
        }
        // 移除IP和Function的对应关系
        ipToFunctions.get(ip).removeAll(functionKeList);
    }

    // IP下线
    public void removeAllRelationsByIp(String ip) {
        log.info("removeAllRelationsByIp-IP {} 下线", ip);
        Set<String> functions = ipToFunctions.get(ip);
        if (functions == null) return;

        // 遍历所有关联的Function
        for (String function : functions) {
            Set<String> ips = functionToIps.get(function);
            if (CollectionUtils.isNotEmpty(ips)) {
                ips.remove(ip);    // 从Function反向索引中移除
                if (ips.isEmpty()) {
                    functionToIps.remove(function); // 清理空集合
                }
            }
        }
        ipToFunctions.remove(ip); // 清除正向索引
    }


    // 批量移除Function所有关联（对称操作）
    public void removeAllRelationsByFunction(String function) {
        Set<String> ips = functionToIps.get(function);
        if (ips == null) return;

        for (String ip : ips) {
            Set<String> functions = ipToFunctions.get(ip);
            if (functions != null) {
                functions.remove(function); // 从IP反向索引中移除
                if (functions.isEmpty()) {
                    ipToFunctions.remove(ip); // 清理空集合
                }
            }
        }
        functionToIps.remove(function);
    }

    public Set<String> queryIpByFunctionKey(String functionKey) {
        Set<String> ips = functionToIps.get(functionKey);
        return ips == null ? Collections.emptySet() : ips;
    }
}
