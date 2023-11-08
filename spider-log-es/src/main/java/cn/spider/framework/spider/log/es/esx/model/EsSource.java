package cn.spider.framework.spider.log.es.esx.model;

import org.noear.snack.ONode;

import java.util.Arrays;

/**
 * ElasticSearch 字段控制
 *
 * @author noear
 * @since 1.0
 */
public class EsSource {
    private final ONode oNode;
    public EsSource(ONode oNode){
        this.oNode = oNode;
    }

    public EsSource includes(String... includes) {
        oNode.getOrNew("includes").addAll(Arrays.asList(includes));
        return this;
    }

    public EsSource excludes(String... includes) {
        oNode.getOrNew("excludes").addAll(Arrays.asList(includes));
        return this;
    }
}
