package cn.spider.framework.domain.area.node;

import cn.spider.framework.domain.area.node.data.SonDomainInfoFunctionModel;

import java.util.List;

public class SonDomainInfoFunction {
    private List<SonDomainInfoFunctionModel> sonDomainFunctionList;

    public List<SonDomainInfoFunctionModel> getSonDomainFunctionList() {
        return sonDomainFunctionList;
    }

    public void setSonDomainFunctionList(List<SonDomainInfoFunctionModel> sonDomainFunctionList) {
        this.sonDomainFunctionList = sonDomainFunctionList;
    }
}
