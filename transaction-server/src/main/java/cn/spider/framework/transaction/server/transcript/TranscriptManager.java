package cn.spider.framework.transaction.server.transcript;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.transaction.server.transcript
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-25  22:21
 * @Description: TODO
 * @Version: 1.0
 */
public class TranscriptManager {

    private Set<String> transcripts;

    public TranscriptManager() {
        this.transcripts = Sets.newHashSet();
    }

    public Boolean checkIsTranscript(String brokerName) {
        return transcripts.contains(transcripts);
    }

    public void replace(Set<String> transcripts) {
        this.transcripts = Objects.isNull(transcripts) ? Sets.newHashSet() : transcripts;
    }
}
