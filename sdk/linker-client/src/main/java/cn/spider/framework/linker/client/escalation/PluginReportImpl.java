package cn.spider.framework.linker.client.escalation;

import cn.spider.framework.linker.client.plugin.PluginReport;
import cn.spider.framework.linker.client.socket.SocketManager;
import cn.spider.framework.linker.sdk.data.emuns.FunctionEscalationType;
import cn.spider.framework.param.result.build.model.NodeParamInfoBath;
import com.alibaba.fastjson.JSON;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Objects;


/**
 * 用于接受插件的上报
 */
@Slf4j
public class PluginReportImpl implements PluginReport {

    private SocketManager socketManager;

    private ApplicationContext context;

    public PluginReportImpl(ApplicationContext context) {
        this.context = context;
    }

    public void init() {
        if (Objects.nonNull(socketManager)) {
            return;
        }
        this.socketManager = context.getBean(SocketManager.class);
    }

    /**
     * 上报插件信息
     *
     * @param areaFunctionParam
     */
    @Override
    public void escalationPlugInParam(NodeParamInfoBath areaFunctionParam) {
        // 告知spider
        log.info("notify_spider_info {}", JSON.toJSONString(areaFunctionParam));
        socketManager.escalationAreaFunctionInfo(JsonObject.mapFrom(areaFunctionParam), FunctionEscalationType.DEPLOY);
    }
}
