package cn.spider.framework.domain.area.node.entity;

import cn.spider.framework.domain.area.node.SonDomainInfoFunction;
import cn.spider.framework.domain.area.node.TestCase;
import cn.spider.framework.domain.area.node.data.FunctionFunctional;
import cn.spider.framework.domain.area.node.data.TableAnalysisModel;
import cn.spider.framework.domain.area.node.data.enums.NodeStatus;
import cn.spider.framework.domain.sdk.data.ParamPack;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;

import java.util.Date;

/**
 * <p>
 * spider-领域功能版本
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */

@TableName(value = "spider_area_function_version", autoResultMap = true)
public class SpiderAreaFunctionVersion{

    private String id;

    /**
     * 领域功能id
     */
    private String domainFunctionId;

    /**
     * 功能名称
     */
    private String domainFunctionName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 描述
     */
    private String versionDesc;

    /**
     * 功能需求
     */
    @TableField(value = "function_functional", typeHandler = FastjsonTypeHandler.class)
    private FunctionFunctional functionFunctional;

    /**
     * 测试场景
     */
    @TableField(value = "test_case", typeHandler = FastjsonTypeHandler.class)
    private TestCase testCase;

    /**
     * 返回的字段信息
     */
    @TableField(value = "result_mapping", typeHandler = FastjsonTypeHandler.class)
    private ParamPack resultMapping;

    /**
     * 执行参数
     */
    @TableField(value = "run_mapping", typeHandler = FastjsonTypeHandler.class)
    private ParamPack runMapping;

    /**
     * 状态
     */
    private NodeStatus status;

    /**
     * 部署数量
     */
    private Integer predictDeployNum;

    @TableField(value = "son_domain_functions", typeHandler = FastjsonTypeHandler.class)
    private SonDomainInfoFunction sonDomainFunctions;

    @TableField(value = "table_analysis_info", typeHandler = FastjsonTypeHandler.class)
    private TableAnalysisModel tableAnalysisInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    private Integer dataFlowId;

    private String dataFlowName;

    public Integer getDataFlowId() {
        return dataFlowId;
    }

    public void setDataFlowId(Integer dataFlowId) {
        this.dataFlowId = dataFlowId;
    }

    public String getDataFlowName() {
        return dataFlowName;
    }

    public void setDataFlowName(String dataFlowName) {
        this.dataFlowName = dataFlowName;
    }

    public Integer getPredictDeployNum() {
        return predictDeployNum;
    }

    public void setPredictDeployNum(Integer predictDeployNum) {
        this.predictDeployNum = predictDeployNum;
    }

    public String getDomainFunctionId() {
        return domainFunctionId;
    }

    public void setDomainFunctionId(String domainFunctionId) {
        this.domainFunctionId = domainFunctionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }


    public ParamPack getResultMapping() {
        return resultMapping;
    }

    public void setResultMapping(ParamPack resultMapping) {
        this.resultMapping = resultMapping;
    }

    public ParamPack getRunMapping() {
        return runMapping;
    }

    public void setRunMapping(ParamPack runMapping) {
        this.runMapping = runMapping;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FunctionFunctional getFunctionFunctional() {
        return functionFunctional;
    }

    public void setFunctionFunctional(FunctionFunctional functionFunctional) {
        this.functionFunctional = functionFunctional;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public String getDomainFunctionName() {
        return domainFunctionName;
    }

    public void setDomainFunctionName(String domainFunctionName) {
        this.domainFunctionName = domainFunctionName;
    }

    public SonDomainInfoFunction getSonDomainFunctions() {
        return sonDomainFunctions;
    }

    public void setSonDomainFunctions(SonDomainInfoFunction sonDomainFunctions) {
        this.sonDomainFunctions = sonDomainFunctions;
    }

    public TableAnalysisModel getTableAnalysisInfo() {
        return tableAnalysisInfo;
    }

    public void setTableAnalysisInfo(TableAnalysisModel tableAnalysisInfo) {
        this.tableAnalysisInfo = tableAnalysisInfo;
    }
}
