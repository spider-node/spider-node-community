package cn.spider.framework.domain.area.function.data;

import cn.spider.framework.domain.area.function.enums.GenerateCoderType;

import java.util.List;

public class GenerateJavaCodeResult {
    private String functionVersionId;

    private GenerateCoderType generateCoderType;

    private List<String> codes;

    public String getFunctionVersionId() {
        return functionVersionId;
    }

    public void setFunctionVersionId(String functionVersionId) {
        this.functionVersionId = functionVersionId;
    }

    public GenerateCoderType getGenerateCoderType() {
        return generateCoderType;
    }

    public void setGenerateCoderType(GenerateCoderType generateCoderType) {
        this.generateCoderType = generateCoderType;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}
