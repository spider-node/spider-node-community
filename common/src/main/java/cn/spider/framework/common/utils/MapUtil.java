package cn.spider.framework.common.utils;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;


public class MapUtil {
    // value为空的值去除
    public static Map<String,Object> removeNullValue(Map<String,Object> param){
        if(param.isEmpty()){
            return param;
        }
        Map<String,Object> map = Maps.newHashMap();
        param.forEach((key,value)->{
            if(Objects.isNull(value)){
                return;
            }
            map.put(key,value);
        });
        return map;
    }
}
