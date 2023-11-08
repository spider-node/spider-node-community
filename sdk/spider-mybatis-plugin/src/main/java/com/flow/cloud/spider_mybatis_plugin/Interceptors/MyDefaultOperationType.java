package com.flow.cloud.spider_mybatis_plugin.Interceptors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: transactiona-test
 * @description:
 * @author: dds
 * @create: 2022-08-04 15:58
 */
public abstract class MyDefaultOperationType implements MyOperationType{
    Map<String, List<String>> encMaps = new HashMap<>();

    /**
     * 假设只对t_user表的username和phone2个字段进行加密存储
     */
    public MyDefaultOperationType(){
        encMaps.put("t_user", Arrays.asList("username","phone"));
    }

    @Override
    public Map<String, List<String>> getEncMap() {
        return encMaps;
    }
}

