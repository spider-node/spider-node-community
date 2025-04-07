package cn.spider.framework.domain.area.timer;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.node.NodeManger;
import cn.spider.framework.domain.area.task.service.ISpiderTaskTestInfoService;
import cn.spider.framework.domain.area.util.LockManager;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoderTimer {
    private Vertx vertx;

    private ISpiderTaskTestInfoService taskTestInfoService;

    private LockManager lockManager;

    public CoderTimer(Vertx vertx, ISpiderTaskTestInfoService taskTestInfoService) {
        this.vertx = vertx;
        this.taskTestInfoService = taskTestInfoService;
        runCase();
        checkDeployInfo();
    }

    public void init(LockManager lockManager){
        this.lockManager = lockManager;
    }

    public void runCase() {
        // 设置每20秒执行一次
        vertx.setPeriodic(1000 * 10, taskId -> {
            taskTestInfoService.runCase();
        });
    }

    public void unLockCodeInfo(String key){
        // 设置15秒后执行一次
        vertx.setPeriodic(1000 * 20, taskId -> {
            lockManager.unLock(key);
        });
    }

    public void checkDeployInfo() {
        // 设置20秒后执行一次
        /*vertx.setPeriodic(1000 * 40, taskId -> {
            nodeManger.checkDeployInfo().onFailure(fail -> {
                log.error("checkDeployInfo fail {}", ExceptionMessage.getStackTrace(fail));
                checkDeployInfo();
            });
        });*/

    }
}
