package cn.spider.framework.domain.area.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 领域功能代码
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */
@TableName("spider_task_coder_info")
public class SpiderTaskCoderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 入参
     */
    private String inputParam;

    /**
     * 出参
     */
    private String outParam;

    /**
     * 代码
     */
    private String businessCoder;

    /**
     * mvn 依赖
     */
    private String mvn;

    /**
     * 任务id
     */
    private Integer taskId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInputParam() {
        return inputParam;
    }

    public void setInputParam(String inputParam) {
        this.inputParam = inputParam;
    }

    public String getOutParam() {
        return outParam;
    }

    public void setOutParam(String outParam) {
        this.outParam = outParam;
    }

    public String getBusinessCoder() {
        return businessCoder;
    }

    public void setBusinessCoder(String businessCoder) {
        this.businessCoder = businessCoder;
    }

    public String getMvn() {
        return mvn;
    }

    public void setMvn(String mvn) {
        this.mvn = mvn;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "SpiderTaskCoderInfo{" +
            "id = " + id +
            ", inputParam = " + inputParam +
            ", outParam = " + outParam +
            ", businessCoder = " + businessCoder +
            ", mvn = " + mvn +
            ", taskId = " + taskId +
        "}";
    }
}
