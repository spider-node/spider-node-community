package cn.spider.framework.flow.delayQueue;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.delayQueue
 * @Author: dengdongsheng
 * @CreateTime: 2023-07-05  15:01
 * @Description: TODO
 * @Version: 1.0
 */
public interface DelayHandler<T> {

    void execute(T t);
}
