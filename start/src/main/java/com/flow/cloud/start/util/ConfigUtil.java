package com.flow.cloud.start.util;

import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: com.flow.cloud.start.util
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-18  13:35
 * @Description: TODO
 * @Version: 1.0
 */
public class ConfigUtil {
    public static Map<String, String> queryZkAddr(){
        Map<String, String> spiderConf = PropertyReader.GetAllProperties("spiderConf.properties");
        switch (spiderConf.get("environment")){
            case "dev":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-dev.properties"));
                break;
            case "qa":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-qa.properties"));
                break;
            case "prod":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-prod.properties"));
                break;
            case "local":
                spiderConf.putAll(PropertyReader.GetAllProperties("spiderConf-local.properties"));
        }
        return spiderConf;
    }
}
