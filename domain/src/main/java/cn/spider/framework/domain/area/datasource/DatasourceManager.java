package cn.spider.framework.domain.area.datasource;
import cn.spider.framework.domain.area.datasource.data.QueryDatasourceParam;
import cn.spider.framework.domain.area.datasource.data.QueryDatasourceResult;
import cn.spider.framework.domain.area.datasource.data.QueryTableInfoParam;
import cn.spider.framework.domain.area.datasource.data.QueryTableInfoResult;
import cn.spider.framework.domain.area.datasource.entity.AreaDatasourceInfo;
import cn.spider.framework.domain.area.datasource.service.IAreaDatasourceInfoService;
import cn.spider.framework.domain.area.util.DatasourceUtil;
import cn.spider.framework.domain.area.util.TableInfo;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;

/**
 * 数据源信息管理
 */
public class DatasourceManager {

    private IAreaDatasourceInfoService datasourceInfoService;

    public DatasourceManager(IAreaDatasourceInfoService datasourceInfoService) {
        this.datasourceInfoService = datasourceInfoService;
    }

    // 获取数据源信息
    public QueryDatasourceResult queryDatasource(QueryDatasourceParam param) {
        List<AreaDatasourceInfo> datasourceInfos = datasourceInfoService.lambdaQuery()
                .likeLeft(AreaDatasourceInfo::getDatasource, param.getDatasource())
                .list();
        return new QueryDatasourceResult(datasourceInfos);
    }

    // 获取表信息
    public QueryTableInfoResult queryTableInfos(QueryTableInfoParam param) {
        AreaDatasourceInfo areaDatasourceInfo = datasourceInfoService
                .lambdaQuery()
                .eq(AreaDatasourceInfo::getDatasource, param.getDatasource())
                .one();
        List<TableInfo> tableInfos = DatasourceUtil.queryTableInfos(areaDatasourceInfo.getUrl(), areaDatasourceInfo.getName(), areaDatasourceInfo.getPassword());
        if(CollectionUtils.isEmpty(tableInfos)){
            return new QueryTableInfoResult();
        }
        tableInfos.forEach(item->{
            item.setDatasource(areaDatasourceInfo.getDatasource());
        });
        return new QueryTableInfoResult(tableInfos);
    }
}
