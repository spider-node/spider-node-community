package cn.spider.framework.transaction.sdk.data;

import java.util.Set;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-25  22:28
 * @Description: TODO
 * @Version: 1.0
 */
public class NotifyTranscriptsChange {

    private Set<String> transcript;

    public NotifyTranscriptsChange(Set<String> transcript) {
        this.transcript = transcript;
    }

    public NotifyTranscriptsChange(){}

    public Set<String> getTranscript() {
        return transcript;
    }

    public void setTranscript(Set<String> transcript) {
        this.transcript = transcript;
    }
}
