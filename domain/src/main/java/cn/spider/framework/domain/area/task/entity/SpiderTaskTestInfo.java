package cn.spider.framework.domain.area.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 测试用例的信息
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */
@TableName("spider_task_test_info")
public class SpiderTaskTestInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 测试用例
     */
    private String cases;

    /**
     * 入参
     */
    private String caseInputParam;

    /**
     * sql
     */
    private String caseSql;

    /**
     * sql参数
     */
    private String caseSqlParam;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * PASS(通过)/REJECT(驳回)
     */
    private String testStatus;

    /**
     * 异常
     */
    private String error;

    /**
     * 驳回原因
     */
    private String rejectReason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getCaseInputParam() {
        return caseInputParam;
    }

    public void setCaseInputParam(String caseInputParam) {
        this.caseInputParam = caseInputParam;
    }

    public String getCaseSql() {
        return caseSql;
    }

    public void setCaseSql(String caseSql) {
        this.caseSql = caseSql;
    }

    public String getCaseSqlParam() {
        return caseSqlParam;
    }

    public void setCaseSqlParam(String caseSqlParam) {
        this.caseSqlParam = caseSqlParam;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
