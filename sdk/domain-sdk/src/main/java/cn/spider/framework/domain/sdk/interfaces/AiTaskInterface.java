package cn.spider.framework.domain.sdk.interfaces;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface AiTaskInterface {
    String ADDRESS = "AI_TASK";

    static AiTaskInterface createProxy(Vertx vertx, String address) {
        return new AiTaskInterfaceVertxEBProxy(vertx, address);
    }

    /**
     * 发起代码生成的任务
     * @param param 参数
     * @return 返回一个空的future 这是个异步的任务
     */
    Future<Void> createCoder(JsonObject param);

    /**
     * 发起 用例测试
     * @param param 用例的参数
     * @return 返回一个空的Future 这是个异步的任务
     */
    Future<Void> startTestCase(JsonObject param);

    /**
     * 查询用例信息
     */
    Future<JsonObject> queryTestCaseInfo(JsonObject param);

    /**
     *  重新执行case
     * @param param 用例的参数
     * @return 返回一个空的Future 这是个异步的任务
     */
    Future<Void> restartCase(JsonObject param);

    /**
     * 通过 ai生成代码任务的步骤
     * @param param 步骤信息
     * @return 空的future
     */
    Future<Void> syncAiCoderStep(JsonObject param);

    /**
     * 查看任务步骤
     */
    Future<JsonObject> queryTaskStep(JsonObject param);
}
