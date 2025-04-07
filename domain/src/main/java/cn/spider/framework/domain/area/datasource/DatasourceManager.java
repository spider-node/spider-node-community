package cn.spider.framework.domain.area.datasource;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.common.utils.JdbcUtil;
import cn.spider.framework.domain.area.datasource.data.*;
import cn.spider.framework.domain.area.datasource.entity.AreaDatasourceInfo;
import cn.spider.framework.domain.area.datasource.service.IAreaDatasourceInfoService;
import cn.spider.framework.domain.area.util.DatasourceUtil;
import cn.spider.framework.domain.area.util.TableInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源信息管理
 */
@Slf4j
public class DatasourceManager {

    private Map<String, MySQLPool> datasourcePoolMap;

    private IAreaDatasourceInfoService datasourceInfoService;

    private Vertx vertx;

    public DatasourceManager(IAreaDatasourceInfoService datasourceInfoService,Vertx vertx) {
        this.datasourceInfoService = datasourceInfoService;
        this.datasourcePoolMap = new HashMap<>();
        this.vertx = vertx;
    }

    // 获取数据源信息
    public QueryDatasourceResult queryDatasource(QueryDatasourceParam param) {
        List<AreaDatasourceInfo> datasourceInfos = datasourceInfoService.lambdaQuery()
                .likeRight(StringUtils.isNotEmpty(param.getDatasource()), AreaDatasourceInfo::getDatasource, param.getDatasource())
                .list();

        return new QueryDatasourceResult(datasourceInfos, 0L);
    }

    public QueryDatasourceResult queryDatasourceResultPage(QueryDatasourceParam param) {
        Page<AreaDatasourceInfo> rowPage = new Page(param.getPage(), param.getSize());
        LambdaQueryWrapper<AreaDatasourceInfo> queryWrapper = new LambdaQueryWrapper<AreaDatasourceInfo>()
                .likeRight(StringUtils.isNotEmpty(param.getDatasource()), AreaDatasourceInfo::getDatasource, param.getDatasource());
        IPage page = datasourceInfoService.page(rowPage, queryWrapper);
        return new QueryDatasourceResult(page.getRecords(), page.getTotal());
    }


    // 获取表信息
    public QueryTableInfoResult queryTableInfos(QueryTableInfoParam param) {
        AreaDatasourceInfo areaDatasourceInfo = datasourceInfoService
                .lambdaQuery()
                .eq(AreaDatasourceInfo::getDatasource, param.getDatasource())
                .one();
        List<TableInfo> tableInfos = DatasourceUtil.queryTableInfos(areaDatasourceInfo.getUrl(), areaDatasourceInfo.getName(), areaDatasourceInfo.getPassword());
        if (CollectionUtils.isEmpty(tableInfos)) {
            return new QueryTableInfoResult();
        }
        tableInfos.forEach(item -> {
            item.setDatasource(areaDatasourceInfo.getDatasource());
        });
        return new QueryTableInfoResult(tableInfos);
    }

    public void upsertDatasource(AreaDatasourceInfo datasourceInfo) {
        datasourceInfoService.saveOrUpdate(datasourceInfo);
    }

    public Future<Void> runUpdateSql(RunGeneralSQLModel runGeneralSQLModel) {
        Promise<Void> promise = Promise.promise();
        MySQLPool mySQLPool = datasourcePoolMap.containsKey(runGeneralSQLModel.getDatasource()) ?
                datasourcePoolMap.get(runGeneralSQLModel.getDatasource()) :
                getMySQLPool(runGeneralSQLModel.getDatasource());
        SqlTemplate
                .forUpdate(mySQLPool, runGeneralSQLModel.getSql())
                .execute(runGeneralSQLModel.getParams())
                .onSuccess(result -> {
                    promise.complete();
                }).onFailure(fail -> {
                    promise.fail(fail);
                    log.info("----执行异常 {}", ExceptionMessage.getStackTrace(fail));

                });
        return promise.future();
    }

    private MySQLPool getMySQLPool(String datasource) {
        AreaDatasourceInfo areaDatasourceInfo = datasourceInfoService.lambdaQuery().eq(AreaDatasourceInfo::getDatasource, datasource).one();
        // 获取jdbc:mysql://47.109.67.130:3306/spider_demo中的ip
        String ip = areaDatasourceInfo.getUrl().split("//")[1].split(":")[0];
        // 获取jdbc:mysql://47.109.67.130:3306/spider_demo中的端口号
        String port = areaDatasourceInfo.getUrl().split("//")[1].split(":")[1].split("/")[0];

        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost(ip)
                .setPassword(areaDatasourceInfo.getPassword())
                .setUser(areaDatasourceInfo.getName())
                .setPort(Integer.parseInt(port))
                .setDatabase(areaDatasourceInfo.getDatasource());

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setEventLoopSize(10)
                .setIdleTimeout(10 * 1000)
                .setPoolCleanerPeriod(5 * 1000)
                .setMaxSize(5);
        MySQLPool client = MySQLPool.pool(this.vertx, connectOptions, poolOptions);
        this.datasourcePoolMap.put(datasource, client);
        // Create the pooled client
        return client;
    }

    /**
     * 查询数据源 基于url拆分
     * @param datasource
     * @return
     */
    public String queryDatasourceId(String datasource){
        AreaDatasourceInfo areaDatasourceInfo = datasourceInfoService.lambdaQuery().eq(AreaDatasourceInfo::getDatasource, datasource).one();
        return JdbcUtil.buildResourceId(areaDatasourceInfo.getUrl());
    }


}
