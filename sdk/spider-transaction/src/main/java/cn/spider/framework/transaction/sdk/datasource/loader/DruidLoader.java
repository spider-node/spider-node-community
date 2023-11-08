package cn.spider.framework.transaction.sdk.datasource.loader;
import java.net.URL;
/**
 * @program: flow-cloud
 * @description:
 * @author: dds
 * @create: 2022-07-26 21:09
 */
public interface DruidLoader {
    /**
     * Gets embedded druid.jar
     *
     * @return embedded druid.jar url
     */
    URL getEmbeddedDruidLocation();
}
