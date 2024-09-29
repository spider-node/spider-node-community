package cn.spider.framework.domain.area.util;

public class ClassUtil {
    /**
     * 将给定字符串的首字母转换成小写。
     *
     * @param str 需要转换的原始字符串
     * @return 转换后的字符串
     */
    public static String toLowerFirstChar(String str) {
        if (str == null || str.isEmpty()) {
            return str; // 如果字符串为空或null，则直接返回
        }
        // 获取第一个字符，并将其转换为小写
        char firstChar = Character.toLowerCase(str.charAt(0));
        // 如果字符串只有一个字符，那么直接返回该字符的小写形式
        if (str.length() == 1) {
            return String.valueOf(firstChar);
        }
        // 否则，拼接第一个字符（已转换为小写）与剩下的部分
        return firstChar + str.substring(1);
    }
}
