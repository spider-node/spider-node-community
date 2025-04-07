package cn.spider.framework.common.utils;

public class NumberUtil {

    // 其他已有的方法... underline
    private final static String UNDERLINE = "_";

    /**
     * 将字符串转换为一个固定的long类型值
     *
     * @param str 输入的字符串
     * @return 对应的long类型值
     */
    public static long stringToLong(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        return str.hashCode();
    }

    public static long stringToLong(String requestId, String transactionId) {
        String branchId = requestId + UNDERLINE + transactionId;
        return stringToLong(branchId);
    }

    // 其他已有的方法...
}