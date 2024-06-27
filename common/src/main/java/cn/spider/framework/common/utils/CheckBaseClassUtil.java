package cn.spider.framework.common.utils;

import org.apache.commons.lang3.StringUtils;


public class CheckBaseClassUtil {
    public static Boolean checkBaseClassType(String className){
        if(StringUtils.equals(className,"java.lang.String")){
            return true;
        }
        return false;
    }
}
