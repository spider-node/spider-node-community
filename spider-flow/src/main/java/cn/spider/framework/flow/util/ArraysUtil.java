package cn.spider.framework.flow.util;

import java.util.Arrays;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.util
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-14  17:41
 * @Description: TODO
 * @Version: 1.0
 */
public class ArraysUtil {
    public static Object[] removeLastElement(Object[] arr) {
        return Arrays.copyOf(arr, arr.length - 1);
    }
}
