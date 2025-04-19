package cn.spider.framework.domain.area.function.data;

import cn.spider.framework.domain.area.function.enums.GenerateCoderType;
import cn.spider.framework.domain.area.function.version.enums.ToJavaEntitySource;

import java.util.List;
import java.util.Map;

public class GenerateJavaCodeParam {
    private String functionVersionId;

    private GenerateCoderType generateCoderType;

    private ToJavaEntitySource source;

    private Map<String, List<FunctionParamConfigModel>> runObjectConfig;

    private Integer httpFunctionId;

    public GenerateJavaCodeParam(String functionVersionId, Map<String, List<FunctionParamConfigModel>> runObjectConfig, GenerateCoderType generateCoderType,ToJavaEntitySource source,Integer httpFunctionId) {
        this.generateCoderType = generateCoderType;
        this.functionVersionId = functionVersionId;
        this.runObjectConfig = runObjectConfig;
        this.source = source;
        this.httpFunctionId = httpFunctionId;
    }

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public Map<String, List<FunctionParamConfigModel>> getRunObjectConfig() {
        return runObjectConfig;
    }

    public void setRunObjectConfig(Map<String, List<FunctionParamConfigModel>> runObjectConfig) {
        this.runObjectConfig = runObjectConfig;
    }

    public GenerateCoderType getGenerateCoderType() {
        return generateCoderType;
    }

    public void setGenerateCoderType(GenerateCoderType generateCoderType) {
        this.generateCoderType = generateCoderType;
    }

    public ToJavaEntitySource getSource() {
        return source;
    }

    public void setSource(ToJavaEntitySource source) {
        this.source = source;
    }

    public Integer getHttpFunctionId() {
        return httpFunctionId;
    }

    public void setHttpFunctionId(Integer httpFunctionId) {
        this.httpFunctionId = httpFunctionId;
    }
}
