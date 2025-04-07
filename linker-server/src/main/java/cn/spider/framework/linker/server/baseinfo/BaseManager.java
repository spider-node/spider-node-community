package cn.spider.framework.linker.server.baseinfo;

import cn.spider.framework.common.utils.TaskKeyUtil;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.param.result.build.model.ReportParamInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;

@Slf4j
public class BaseManager {
    private BaseMate baseMate;

    public BaseManager() {
        this.baseMate = new BaseMate();
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
                baseMate.deploy(refreshAreaParam, ip);
                break;
            case UNLOCK:
                baseMate.offline(refreshAreaParam, ip);
                break;
        }
    }

    /**
     * ip下线
     */
    public void offlineIp(String ip) {
        baseMate.removeAllRelationsByIp(ip);
    }

    /**
     * @param componentName   组件名称
     * @param taskServiceName 方法名称
     * @param version         版本
     * @return 返回ip集合
     * @throws Exception 异常信息
     */
    public Set<String> queryIpByFunctionKey(String componentName, String taskServiceName, String version) {
        String functionKey = TaskKeyUtil.buildComponentKey(componentName, taskServiceName, version);
        log.info("查询任务部署信息，组件：{} 服务：{} 版本：{}", componentName, taskServiceName, version);

        return baseMate.queryIpByFunctionKey(functionKey);
    }


}
