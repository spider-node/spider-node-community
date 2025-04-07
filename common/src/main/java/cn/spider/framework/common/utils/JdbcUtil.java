package cn.spider.framework.common.utils;

public class JdbcUtil {
    public static String buildResourceId(String jdbcUrl) {
        if (jdbcUrl.contains("?")) {
            return jdbcUrl.substring(0, jdbcUrl.indexOf('?'));
        }
        return jdbcUrl;
    }
}
