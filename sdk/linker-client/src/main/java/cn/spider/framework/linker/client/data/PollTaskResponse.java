package cn.spider.framework.linker.client.data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-08  22:28
 * @Description: 轮询task的返回类
 * @Version: 1.0
 */
public class PollTaskResponse {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
