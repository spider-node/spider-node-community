package cn.spider.framework.domain.area.task.entity;

import cn.spider.framework.domain.area.node.data.SonDomainInfo;
import cn.spider.framework.domain.area.task.data.enums.TaskStatus;
import cn.spider.framework.domain.area.task.data.enums.TaskType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 宿主应用
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */
@TableName(value = "spider_domain_function_task", autoResultMap = true)
public class SpiderDomainFunctionTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    @TableField(value = "son_domain_info", typeHandler = FastjsonTypeHandler.class)
    private SonDomainInfo sonDomainInfo;

    /**
     * 领域id
     */
    private String taskDomainId;

    /**
     * 任务类型 NEWLY_ADDED/ITERATION
     */
    private TaskType taskType;

    /**
     * /DATA_INIT(数据准备)/CODING(编码钟)/COMPILE(编译)/TEST_DATA_INIT(测试数据准备)/TEST(测试)/FINISH(完成)
     */
    private TaskStatus status;

    /**
     * 异常信息
     */
    private String error;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 领域功能的版本id
     */
    private String domainFunctionVersionId;

    /**
     * 领域功能id
     */
    private String domainFunctionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskDomainId() {
        return taskDomainId;
    }

    public void setTaskDomainId(String taskDomainId) {
        this.taskDomainId = taskDomainId;
    }



    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public String getDomainFunctionId() {
        return domainFunctionId;
    }

    public void setDomainFunctionId(String domainFunctionId) {
        this.domainFunctionId = domainFunctionId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public SonDomainInfo getSonDomainInfo() {
        return sonDomainInfo;
    }

    public void setSonDomainInfo(SonDomainInfo sonDomainInfo) {
        this.sonDomainInfo = sonDomainInfo;
    }
}
