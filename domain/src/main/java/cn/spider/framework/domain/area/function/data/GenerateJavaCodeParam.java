package cn.spider.framework.domain.area.function.data;

import cn.spider.framework.domain.area.function.enums.GenerateCoderType;

import java.util.List;
import java.util.Map;

public class GenerateJavaCodeParam {
    private String functionVersionId;

    private GenerateCoderType generateCoderType;

    private Map<String, List<FunctionParamConfigModel>> runObjectConfig;

    public GenerateJavaCodeParam(String functionVersionId, Map<String, List<FunctionParamConfigModel>> runObjectConfig, GenerateCoderType generateCoderType) {
        this.generateCoderType = generateCoderType;
        this.functionVersionId = functionVersionId;
        this.runObjectConfig = runObjectConfig;
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
}
