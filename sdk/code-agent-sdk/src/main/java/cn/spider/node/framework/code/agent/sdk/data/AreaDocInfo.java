package cn.spider.node.framework.code.agent.sdk.data;
import lombok.Data;
import java.util.List;

@Data
public class AreaDocInfo {
    /**
     * 领域-详情
     */
    private List<AreaInfo> areaInfos;

    /**
     * 领域插件-详情
     */
    private List<AreaPluginInfo> areaPluginInfos;

    /**
     * datasource -信息
     */
    private List<DatasourceInfo> datasourceInfos;

    /**
     * 子域基础信息
     */
    private List<SonAreaCodeBase> sonAreaCodeBases;

    /**
     * 子域配置信息
     */
    private List<SonAreaInfo> sonAreaInfos;
}
