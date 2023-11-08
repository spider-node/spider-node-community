package cn.spider.framework.linker.client.data;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-08  22:30
 * @Description: 轮询状态
 * @Version: 1.0
 */
public enum PollStatus {
    SUSS("suss"),
    FAIL("fail"),
    WAIT("wait")
    ;

    PollStatus(String desc) {
        this.desc = desc;
    }

    private String desc;
}
