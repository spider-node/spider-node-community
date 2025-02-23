package cn.spider.framework.spider.param.engine.util;

public class StringCapitalizer {

    /**
     * 将给定字符串的首字母转换为大写。
     *
     * @param str 需要处理的字符串
     * @return 处理后的字符串，首字母大写，其余保持不变
     */
    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // Convert the first letter to title case and concatenate with the rest of the string.
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
