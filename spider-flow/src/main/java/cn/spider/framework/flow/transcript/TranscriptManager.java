package cn.spider.framework.flow.transcript;

import cn.spider.framework.common.config.Constant;
import cn.spider.framework.common.event.EventManager;
import cn.spider.framework.common.event.EventType;
import cn.spider.framework.common.event.data.LeaderReplaceData;
import cn.spider.framework.common.utils.BrokerInfoUtil;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.flow.timer.SpiderTimer;
import cn.spider.framework.transaction.sdk.data.NotifyTranscriptsChange;
import cn.spider.framework.transaction.sdk.interfaces.TransactionInterface;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.Set;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.transcript
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-25  14:14
 * @Description: 副本管理
 * @Version: 1.0
 */
@Slf4j
public class TranscriptManager {

    private Set<String> transcripts;

    private EventManager eventManager;

    private String thisBrokerName;

    private Vertx vertx;

    private TransactionInterface transactionInterface;

    private SpiderTimer timer;


    public TranscriptManager(EventManager eventManager,
                             Vertx vertx,
                             TransactionInterface transactionInterface,
                             SpiderTimer timer) {
        this.transcripts = Sets.newHashSet();
        this.eventManager = eventManager;
        this.vertx = vertx;
        this.thisBrokerName = BrokerInfoUtil.queryBrokerName(this.vertx);
        this.transactionInterface = transactionInterface;
        this.timer = timer;
    }

    public Boolean checkIsTranscript(String brokerName) {
        return transcripts.contains(brokerName);
    }

    public void replace(Set<String> transcripts) {
        this.transcripts = Objects.isNull(transcripts) ? Sets.newHashSet() : transcripts;
        NotifyTranscriptsChange change = new NotifyTranscriptsChange(this.transcripts);
        Future<Void> future = this.transactionInterface.replaceTranscripts(JsonObject.mapFrom(change));
        future.onFailure(fail->{
            log.error("brokerName {} 同步副本信息 {} 失败 {}",this.thisBrokerName, JSON.toJSONString(this.transcripts), ExceptionMessage.getStackTrace(fail));
        });
    }

    /**
     * 选举某个副本的leader
     * @param brokerName
     */
    public void election(String brokerName) {
    }

}
