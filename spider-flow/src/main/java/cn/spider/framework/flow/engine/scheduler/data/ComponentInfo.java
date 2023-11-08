package cn.spider.framework.flow.engine.scheduler.data;

import lombok.Builder;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.engine.scheduler.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-31  18:52
 * @Description: TODO
 * @Version: 1.0
 */
@Builder
public class ComponentInfo {
    private String workerName;

    private String componentName;

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
