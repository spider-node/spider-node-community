package cn.spider.framework.common.utils;

import java.util.Map;
import java.util.Random;

public class WeightAlgorithm {
    public static String getServerByWeight(Map<String, Integer> map) {
        if (map.isEmpty()) {
            return null;
        }
        Integer total = 0;
        // 计算所有权重
        for (Integer value : map.values()) {
            total += value;
        }

        Random random = new Random();
        // 在权重范围内随机
        int nextInt = random.nextInt(total);

        // 遍历所有服务提供者的startId
        for (String version : map.keySet()) {
            // 取出权重值
            Integer weight = map.get(version);

            // 权重在范围内，则返回对应ip
            if (nextInt < weight) {
                return version;
            }
            // 否则减去权重，继续下一次循环，匹配对应的version
            nextInt -= weight;
        }
        return null;
    }
}
