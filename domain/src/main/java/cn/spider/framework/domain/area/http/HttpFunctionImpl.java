package cn.spider.framework.domain.area.http;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.http.data.QueryHttpParam;
import cn.spider.framework.domain.area.http.data.QueryHttpResult;
import cn.spider.framework.domain.area.http.entity.SpiderToolHttp;
import cn.spider.framework.domain.area.http.service.ISpiderToolHttpService;
import cn.spider.framework.domain.sdk.interfaces.HttpFunctionInterface;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Executor;

@Slf4j
public class HttpFunctionImpl implements HttpFunctionInterface {

    private ISpiderToolHttpService spiderToolHttpService;

    private Executor spiderBusinessPool;

    public HttpFunctionImpl(ISpiderToolHttpService spiderToolHttpService, Executor spiderBusinessPool) {
        this.spiderToolHttpService = spiderToolHttpService;
        this.spiderBusinessPool = spiderBusinessPool;
    }

    @Override
    public Future<JsonObject> queryHttpFunction(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QueryHttpParam queryHttpParam = data.mapTo(QueryHttpParam.class);
                Page<SpiderToolHttp> rowPage = new Page(queryHttpParam.getPage(), queryHttpParam.getSize());
                LambdaQueryWrapper<SpiderToolHttp> queryWrapper = new LambdaQueryWrapper<SpiderToolHttp>()
                        .eq(StringUtils.isNotEmpty(queryHttpParam.getHttpFunctionName()), SpiderToolHttp::getHttpFunctionName, queryHttpParam.getHttpFunctionName());
                IPage page = spiderToolHttpService.page(rowPage, queryWrapper);
                QueryHttpResult queryHttpResult = new QueryHttpResult(page.getTotal(), page.getRecords());
                promise.complete(JsonObject.mapFrom(queryHttpResult));
            } catch (Exception e) {
                promise.fail(e);
                log.error("queryHttpFunction error", ExceptionMessage.getStackTrace(e));
            }
        });

        return promise.future();
    }

    @Override
    public Future<Void> upsertHttpFunction(JsonObject data) {
        Promise<Void> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                SpiderToolHttp spiderToolHttp = JSON.parseObject(data.toString(), SpiderToolHttp.class);
                spiderToolHttpService.saveOrUpdate(spiderToolHttp);
                promise.complete();
            } catch (Exception e) {
                log.error("upsertHttpFunction_error {}", ExceptionMessage.getStackTrace(e));
                promise.fail(e);
            }
        });
        return promise.future();
    }
}
