package cn.spider.framework.common.utils;

public class PluginKeyUtil {
    public static String buildPluginKey(String bizName,String version){
        return bizName + "#" + version;
    }
}
