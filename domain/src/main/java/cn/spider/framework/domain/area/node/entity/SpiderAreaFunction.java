package cn.spider.framework.domain.area.node.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 域功能
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */
@TableName("spider_area_function")
public class SpiderAreaFunction {

    /**
     * id
     */
    private String id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 领域描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 组件名称
     */
    private String taskComponent;

    /**
     * 组件方法
     */
    private String taskService;

    /**
     * 状态
     */
    private String status;

    /**
     * 方法参数
     */
    private String taskMethod;

    /**
     * 领域id
     */
    private String areaId;

    /**
     * 领域名称
     */
    private String areaName;

    /**
     * 子领域id
     */
    private Integer sonDomainId;

    /**
     * 子域名称
     */
    private String sonDomainName;

    /**
     * 服务id
     */
    private String workerId;

    /**
     * MICROSERVICE(微服务)/HOST_APPLICATION(宿主应用)
     */
    private String workerType;

    /**
     * 创建时间
     */
    private Date createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTaskComponent() {
        return taskComponent;
    }

    public void setTaskComponent(String taskComponent) {
        this.taskComponent = taskComponent;
    }

    public String getTaskService() {
        return taskService;
    }

    public void setTaskService(String taskService) {
        this.taskService = taskService;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getSonDomainId() {
        return sonDomainId;
    }

    public void setSonDomainId(Integer sonDomainId) {
        this.sonDomainId = sonDomainId;
    }

    public String getSonDomainName() {
        return sonDomainName;
    }

    public void setSonDomainName(String sonDomainName) {
        this.sonDomainName = sonDomainName;
    }
}
