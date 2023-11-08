package cn.spider.framework.flow.util;

import cn.spider.framework.flow.bus.BasicStoryBus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.util
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-19  17:00
 * @Description: TODO
 * @Version: 1.0
 */
public class ResultUtil {
    public static Object buildObject(BasicStoryBus storyBus){
        Object result = new Object();
        if (storyBus.getResult().isPresent()) {
            result = storyBus.getResult().get();
        }
        return result;
    }

    public static Object buildObjectMessage(BasicStoryBus storyBus){
        Object result = new Object();
        if (storyBus.getResult().isPresent()) {
            result = storyBus.getResult().get();
        }
        return null;
    }


}
