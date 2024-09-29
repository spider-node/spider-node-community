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

    private SharedData sharedData;

    public TaskTimer(Vertx vertx, TaskManager taskManager) {
        this.vertx = vertx;
        this.taskManager = taskManager;
        this.sharedData = vertx.sharedData();
        runPluginTaskTimer();
    }

    private final String LOCK = "plugin_task_run";

    /**
     * 定时任务执行-寻找任务执行
     */
    private void runPluginTaskTimer() {
        vertx.setPeriodic(20 * 1000, id -> {
            log.info("找任务了");
            taskManager.run();
            /*this.sharedData.getLockWithTimeout(LOCK,1000).onSuccess(suss -> {
                try {
                    log.info("获取锁成功");
                    taskManager.run();
                } catch (Exception e) {
                    log.error("执行任务失败");
                }
                // 注册延迟释放锁
                registerLockRelease(suss);
            }).onFailure(lockFail->{
                log.error("获取锁失败 {}",ExceptionMessage.getStackTrace(lockFail));
            });*/
        });
    }

    /**
     * 提供锁的延迟释放
     * @param lock
     */
    private void registerLockRelease(Lock lock){
        vertx.setTimer(5000,id->{
            lock.release();
        });
    }
}
