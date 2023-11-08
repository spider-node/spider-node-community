package cn.spider.framework.flow.funtion.data;

import cn.spider.framework.common.data.enums.JarStatus;
import io.vertx.sqlclient.templates.RowMapper;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  22:24
 * @Description: TODO
 * @Version: 1.0
 */
public class SdkRow {
    public static RowMapper<Sdk> ROW_BUSINESS = row -> {
        Sdk sdk = new Sdk();
        sdk.setId(row.getString("id"));
        sdk.setClassPath(row.getString("class_path"));
        sdk.setJarName(row.getString("jar_name"));
        sdk.setUrl(row.getString("url"));
        sdk.setStatus(JarStatus.valueOf(row.getString("status")));
        return sdk;
    };
}
