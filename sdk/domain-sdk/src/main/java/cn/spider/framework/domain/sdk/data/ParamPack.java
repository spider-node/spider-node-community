package cn.spider.framework.domain.sdk.data;

import cn.spider.framework.param.result.build.NodeField;

import java.util.List;

public class ParamPack {
    private List<NodeField> inputParamDefs;

    public ParamPack(List<NodeField> inputParamDefs) {
        this.inputParamDefs = inputParamDefs;
    }

    public List<NodeField> getInputParamDefs() {
        return inputParamDefs;
    }

    public void setInputParamDefs(List<NodeField> inputParamDefs) {
        this.inputParamDefs = inputParamDefs;
    }
}
