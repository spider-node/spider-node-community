package cn.spider.framework.domain.area.util;

import cn.spider.framework.domain.area.timer.CoderTimer;
import io.vertx.core.impl.ConcurrentHashSet;

import java.util.HashSet;
import java.util.Set;

public class LockManager {
    private Set<String> keys;

    private CoderTimer coderTimer;

    public LockManager(CoderTimer coderTimer) {
        this.keys = new ConcurrentHashSet<>();
        this.coderTimer = coderTimer;
        this.coderTimer.init(this);
    }

    /**
     * 锁住
     *
     * @param key
     * @return
     */
    public boolean lock(String key) {
        if (this.keys.contains(key)) {
            return false;
        }
        this.keys.add(key);
        this.coderTimer.unLockCodeInfo(key);
        return true;
    }

    /**
     * 释放
     */
    public void unLock(String key) {
        keys.remove(key);
    }
}
