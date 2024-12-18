package cn.spider.framework.domain.area.task.entity;

import cn.spider.framework.domain.area.task.entity.enums.AiCodeStep;
import cn.spider.framework.domain.area.task.entity.enums.StepStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 代码生成过程表
 * </p>
 *
 * @author dds
 * @since 2024-12-16
 */
@TableName("spider_domain_function_ai_coder_step")
public class SpiderDomainFunctionAiCoderStep implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 步骤LOAD_DOMAIN_INFO/CODER/COMPILE/TEST/END
     */
    private AiCodeStep step;

    /**
     * 任务id
     */
    private Integer spiderDomainFunctionTaskId;

    /**
     * 异常信息
     */
    private String error;

    /**
     * 步骤状态
     */
    private StepStatus stepStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AiCodeStep getStep() {
        return step;
    }

    public void setStep(AiCodeStep step) {
        this.step = step;
    }

    public Integer getSpiderDomainFunctionTaskId() {
        return spiderDomainFunctionTaskId;
    }

    public void setSpiderDomainFunctionTaskId(Integer spiderDomainFunctionTaskId) {
        this.spiderDomainFunctionTaskId = spiderDomainFunctionTaskId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public StepStatus getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatus stepStatus) {
        this.stepStatus = stepStatus;
    }
}
