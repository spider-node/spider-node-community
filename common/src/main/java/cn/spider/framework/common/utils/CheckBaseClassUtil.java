package cn.spider.framework.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.utils
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  22:44
 * @Description: TODO
 * @Version: 1.0
 */
public class CheckBaseClassUtil {
    public static Boolean checkBaseClassType(String className){
        if(StringUtils.equals(className,"java.lang.String")){
            return true;
        }
        return false;
    }
}
