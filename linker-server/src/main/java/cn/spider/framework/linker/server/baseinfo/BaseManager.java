package cn.spider.framework.linker.server.baseinfo;

import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.param.result.build.model.ReportParamInfo;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Set;

public class BaseManager {
    private BaseMate baseMate;

    public BaseManager(RocksdbUtil rocksdbUtil) {
        this.baseMate = new BaseMate(rocksdbUtil);
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

    public Set<String> queryIpByFunctionKey(String componentName, String taskServiceName, String version) throws Exception {
        return baseMate.queryIpByFunctionKey(componentName, taskServiceName, version);
    }


}
