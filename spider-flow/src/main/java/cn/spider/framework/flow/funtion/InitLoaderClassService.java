package cn.spider.framework.flow.funtion;
import cn.spider.framework.common.data.enums.JarStatus;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.sdk.data.SdkInfo;
import cn.spider.framework.domain.sdk.data.SdkUrlQueryResult;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.flow.funtion.data.Sdk;
import cn.spider.framework.flow.funtion.data.SdkRow;
import cn.spider.framework.flow.load.loader.ClassLoaderManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-14  01:46
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Component
public class InitLoaderClassService {
    @Autowired
    private ClassLoaderManager classLoaderManager;

    @Autowired
    private MySQLPool client;

    @Autowired
    private AreaInterface areaInterface;

    @Autowired
    private Vertx vertx;

    public void init() {
    }


    public void initSdkInfo() {
        areaInterface.queryAreaSdk().onSuccess(suss -> {
            JsonObject sdkUrls = suss;
            SdkUrlQueryResult sdkUrlQueryResult = sdkUrls.mapTo(SdkUrlQueryResult.class);
            for(SdkInfo sdkInfo : sdkUrlQueryResult.getSdkInfos()){
                classLoaderManager.loaderUrlJar(sdkInfo.getSdkName(), sdkInfo.getSdkScanPatch(), sdkInfo.getUrl());
            }
        }).onFailure(fail -> {
            log.info("获取加载的数据 {}", ExceptionMessage.getStackTrace(fail));
        });
    }

    public void loaderClass() {
        Map<String, Object> param = new HashMap<>();
        SqlTemplate
                .forQuery(client, "SELECT * FROM sdk")
                .mapTo(SdkRow.ROW_BUSINESS)
                .execute(param)
                .onSuccess(sdkSuss -> {
                    RowSet<Sdk> sdks = sdkSuss;
                    sdks.forEach(item -> {
                        Sdk sdk = item;
                        if (Objects.isNull(sdk.getStatus()) || sdk.getStatus().equals(JarStatus.STOP)) {
                            return;
                        }
                        try {
                            classLoaderManager.loaderUrlJar(sdk.getJarName(), sdk.getClassPath(), sdk.getUrl());
                        } catch (Exception e) {
                            log.error("加载失败的 jar {}", sdk.getId());
                        }
                        log.info("加载成功" + sdk.getId());
                    });

                }).onFailure(fail -> {
                    log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(fail));
                });
    }

}