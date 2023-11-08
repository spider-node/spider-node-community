package cn.spider.framework.flow.business.data;
import lombok.Data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-09  14:36
 * @Description: 删除功能列表信息
 * @Version: 1.0
 */
@Data
public class DeleteBusinessFunctionRequest {
    private String functionId;

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }
}
