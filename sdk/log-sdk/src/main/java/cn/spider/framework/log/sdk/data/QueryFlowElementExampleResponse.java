package cn.spider.framework.log.sdk.data;

import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.log.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-18  19:19
 * @Description: 查询流程节点信息的结果返回
 * @Version: 1.0
 */
public class QueryFlowElementExampleResponse {
    private List<FlowElementExample> elementExampleList;

    private long total;

    public List<FlowElementExample> getElementExampleList() {
        return elementExampleList;
    }

    public void setElementExampleList(List<FlowElementExample> elementExampleList) {
        this.elementExampleList = elementExampleList;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
