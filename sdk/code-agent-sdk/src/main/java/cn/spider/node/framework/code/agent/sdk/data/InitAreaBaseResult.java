package cn.spider.node.framework.code.agent.sdk.data;

import lombok.Data;

@Data
public class InitAreaBaseResult {
    /**
     * 子领域-内容
     */
    private SonAreaInfo sonArea;

    /**
     * 领域base信息
     */
    private SonAreaCodeBase areaDomainInfo;
}
