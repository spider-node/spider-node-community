package cn.spider.node.host.plugin.center.timer;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.node.host.plugin.center.task.TaskManager;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时处理-task
 */
@Slf4j
public class TaskTimer {

    private Vertx vertx;

    private TaskManager taskManager;

    public TaskTimer(Vertx vertx, TaskManager taskManager) {
        this.vertx = vertx;
        this.taskManager = taskManager;
        runPluginTaskTimer();
    }

    /**
     * 定时任务执行-寻找任务执行
     */
    private void runPluginTaskTimer() {
        vertx.setPeriodic(20 * 1000, id -> {
            taskManager.run();
        });
    }

    // 新增一个一次性任务 启动的时候，校验版本信息，是否需要重新部署
    public void checkVersion() {
        // 新增一个一次的timer
        vertx.setTimer(1000, id -> {

        });
    }
}
