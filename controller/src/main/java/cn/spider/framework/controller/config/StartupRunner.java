package cn.spider.framework.controller.config;

import cn.spider.framework.controller.election.ElectionLeader;
import cn.spider.framework.controller.follower.FollowerManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 容器初始化完成后执行的逻辑
        ElectionLeader electionLeader = event.getApplicationContext().getBean(ElectionLeader.class);
        // 初始化follower的基础额能力
        FollowerManager followerManager = event.getApplicationContext().getBean(FollowerManager.class);
        followerManager.init();
        // 开始选举
        electionLeader.election();
    }
}
