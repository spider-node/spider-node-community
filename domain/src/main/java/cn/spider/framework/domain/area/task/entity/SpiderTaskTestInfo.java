package cn.spider.framework.domain.area.task.entity;

import cn.spider.framework.domain.area.task.data.CaseSqlModel;
import cn.spider.framework.domain.area.task.data.MethodInputModel;
import cn.spider.framework.domain.area.task.entity.enums.CaseExpect;
import cn.spider.framework.domain.area.task.entity.enums.TestStatus;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 测试用例的信息
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */
@TableName(value = "spider_task_test_info", autoResultMap = true)
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
    @TableField(value = "case_input_param", typeHandler = FastjsonTypeHandler.class)
    private MethodInputModel caseInputParam;

    /**
     * sql
     */
    @TableField(value = "case_sql", typeHandler = FastjsonTypeHandler.class)
    private CaseSqlModel caseSql;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 领域功能id
     */
    private String domainFunctionVersionId;

    /**
     * PASS(通过)/REJECT(驳回)
     */
    private TestStatus testStatus;

    /**
     * 异常
     */
    private String error;

    /**
     * 执行结果
     */
    @TableField(value = "run_result", typeHandler = FastjsonTypeHandler.class)
    private JSONObject runResult;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 结果是否符号预期
     */
    private CaseExpect expect;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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


    public CaseSqlModel getCaseSql() {
        return caseSql;
    }

    public void setCaseSql(CaseSqlModel caseSql) {
        this.caseSql = caseSql;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public TestStatus getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestStatus testStatus) {
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

    public MethodInputModel getCaseInputParam() {
        return caseInputParam;
    }

    public void setCaseInputParam(MethodInputModel caseInputParam) {
        this.caseInputParam = caseInputParam;
    }

    public String getDomainFunctionVersionId() {
        return domainFunctionVersionId;
    }

    public void setDomainFunctionVersionId(String domainFunctionVersionId) {
        this.domainFunctionVersionId = domainFunctionVersionId;
    }

    public JSONObject getRunResult() {
        return runResult;
    }

    public void setRunResult(JSONObject runResult) {
        this.runResult = runResult;
    }

    public CaseExpect getExpect() {
        return expect;
    }

    public void setExpect(CaseExpect expect) {
        this.expect = expect;
    }
}
