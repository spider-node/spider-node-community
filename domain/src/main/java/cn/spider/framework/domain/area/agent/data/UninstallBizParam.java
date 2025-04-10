package cn.spider.framework.domain.area.agent.data;

import lombok.Data;


@Data
public class UninstallBizParam {
    private String bizName;

    private String bizVersion;

    public UninstallBizParam(String bizName, String bizVersion) {
        this.bizName = bizName;
        this.bizVersion = bizVersion;
    }
}
