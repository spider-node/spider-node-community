package cn.spider.framework.flow.funtion.data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-08-07  22:22
 * @Description: 领域模型对象
 * @Version: 1.0
 */
public class AreaModel {
    /**
     * 域名称
     */
    private String areaName;

    /**
     * 域服务
     */
    private String workerService;

    /**
     * 域字段-- 通过json存储
     */
    private String areaField;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getWorkerService() {
        return workerService;
    }

    public void setWorkerService(String workerService) {
        this.workerService = workerService;
    }

    public String getAreaField() {
        return areaField;
    }

    public void setAreaField(String areaField) {
        this.areaField = areaField;
    }
}
