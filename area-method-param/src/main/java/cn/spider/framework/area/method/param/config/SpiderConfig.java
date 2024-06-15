package cn.spider.framework.area.method.param.config;

import cn.spider.framework.area.method.param.MainVerticle;
import cn.spider.framework.area.method.param.analysis.ParamRefreshManager;
import cn.spider.framework.area.method.param.impl.ParamRefreshInterfaceImpl;
import cn.spider.framework.domain.sdk.interfaces.AreaInterface;
import cn.spider.framework.domain.sdk.interfaces.NodeInterface;
import cn.spider.framework.param.result.build.analysis.AnalysisClass;
import cn.spider.framework.param.result.build.interfaces.ParamRefreshInterface;
import io.vertx.core.Vertx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"cn.spider.framework.area.method.param.*"})
public class SpiderConfig {

    @Bean
    public Vertx getVertx() {
        return MainVerticle.clusterVertx;
    }

    @Bean
    public AnalysisClass buildAnalysisClass() {
        return new AnalysisClass();
    }

    @Bean
    public NodeInterface buildNodeInterface(Vertx vertx) {
        return NodeInterface.createProxy(vertx,NodeInterface.ADDRESS);
    }

    @Bean
    public AreaInterface buildAreaInterface(Vertx vertx){
        return AreaInterface.createProxy(vertx,AreaInterface.ADDRESS);
    }


    @Bean
    public ParamRefreshManager buildParamRefreshManager(AnalysisClass analysisClass,NodeInterface nodeInterface,AreaInterface areaInterface){
        return new ParamRefreshManager(analysisClass,nodeInterface,areaInterface);
    }

    @Bean
    public ParamRefreshInterface buildParamRefreshInterface(ParamRefreshManager paramRefreshManager){
        return new ParamRefreshInterfaceImpl(paramRefreshManager);
    }

}
