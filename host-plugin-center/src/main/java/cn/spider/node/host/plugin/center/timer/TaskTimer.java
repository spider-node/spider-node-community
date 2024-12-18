package cn.spider.node.host.plugin.center.timer;
import cn.spider.node.host.plugin.center.task.TaskManager;
import io.vertx.core.Vertx;
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
        vertx.setPeriodic(30 * 1000, id -> {
            taskManager.run();
        });
    }
}
