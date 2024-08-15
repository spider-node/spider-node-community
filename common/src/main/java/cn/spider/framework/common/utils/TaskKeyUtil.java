package cn.spider.framework.common.utils;

public class TaskKeyUtil {

    private static final String segmentation = "#";

    public static String buildTaskKey(String taskComponent,String taskService){
        return taskComponent + segmentation + taskComponent;
    }
}
