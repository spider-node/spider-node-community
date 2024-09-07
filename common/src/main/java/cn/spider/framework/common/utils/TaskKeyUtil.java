package cn.spider.framework.common.utils;

public class TaskKeyUtil {

    private static final String segmentation = "#";
    //partition
    private static final String VERSION_PARTITION = "@";

    public static String buildTaskKey(String taskComponent,String taskService,String version){
        return taskComponent + segmentation + taskService + VERSION_PARTITION + version;
    }

    public static String buildTaskKey(String taskComponent,String taskService){
        return taskComponent + segmentation + taskService;
    }
}
