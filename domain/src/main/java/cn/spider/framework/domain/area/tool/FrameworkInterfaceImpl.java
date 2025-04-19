package cn.spider.framework.domain.area.tool;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.domain.area.tool.data.QueryFrameworkResult;
import cn.spider.framework.domain.area.tool.entity.SpiderToolFramework;
import cn.spider.framework.domain.area.tool.service.ISpiderToolFrameworkService;
import cn.spider.framework.domain.sdk.data.QueryFrameworkParam;
import cn.spider.framework.domain.sdk.interfaces.FrameworkInterface;
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
public class FrameworkInterfaceImpl implements FrameworkInterface {

    private ISpiderToolFrameworkService spiderToolFrameworkService;

    private Executor spiderBusinessPool;

    public FrameworkInterfaceImpl(ISpiderToolFrameworkService spiderToolFrameworkService, Executor spiderBusinessPool) {
        this.spiderToolFrameworkService = spiderToolFrameworkService;
        this.spiderBusinessPool = spiderBusinessPool;
    }

    @Override
    public Future<JsonObject> queryFramework(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        spiderBusinessPool.execute(() -> {
            try {
                QueryFrameworkParam param = data.mapTo(QueryFrameworkParam.class);
                Page<SpiderToolFramework> rowPage = new Page(param.getPage(), param.getSize());
                LambdaQueryWrapper<SpiderToolFramework> queryWrapper = new LambdaQueryWrapper<SpiderToolFramework>()
                        .eq(StringUtils.isNotEmpty(param.getFrameworkName()), SpiderToolFramework::getFrameworkName, param.getFrameworkName());
                IPage page = spiderToolFrameworkService.page(rowPage, queryWrapper);
                QueryFrameworkResult queryFrameworkResult = new QueryFrameworkResult(page.getTotal(), page.getRecords());
                promise.complete(JsonObject.mapFrom(queryFrameworkResult));
            } catch (Exception e) {
                promise.fail(e);
                log.error("queryFramework error", ExceptionMessage.getStackTrace(e));
            }
        });

        return promise.future();
    }

    @Override
    public Future<Void> insertFramework(JsonObject data) {
        SpiderToolFramework spiderToolFramework = data.mapTo(SpiderToolFramework.class);
        spiderToolFrameworkService.saveOrUpdate(spiderToolFramework);
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> updateFramework(JsonObject data) {
        return null;
    }
}
