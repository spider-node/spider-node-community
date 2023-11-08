package cn.spider.framework.spider.log.es.esx.model;
import org.noear.snack.ONode;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.0.3
 */
public class EsTerms {
    private final ONode oNode;

    public EsTerms(ONode oNode) {
        this.oNode = oNode;
    }

    public EsTerms include(String expr) {
        oNode.set("include", expr);
        return this;
    }

    public EsTerms include(String[] fields) {
        oNode.getOrNew("include").addAll(Arrays.asList(fields));
        return this;
    }

    public EsTerms exclude(String expr) {
        oNode.set("exclude", expr);
        return this;
    }

    public EsTerms exclude(String[] fields) {
        oNode.getOrNew("exclude").addAll(Arrays.asList(fields));
        return this;
    }

    public EsTerms size(int size) {
        oNode.set("size", size);
        return this;
    }

    public EsTerms shardSize(int size) {
        oNode.set("shard_size", size);
        return this;
    }

    /**
     * 聚合模式
     */
    public EsTerms collectMode(String collect_mode) {
        oNode.set("collect_mode", collect_mode);
        return this;
    }

    /**
     * 缺省值
     */
    public EsTerms missing(String missing) {
        oNode.set("missing", missing);
        return this;
    }

    /**
     * 排序
     */
    public EsTerms sort(Consumer<EsSort> sort) {
        EsSort s = new EsSort(oNode.getOrNew("sort").asArray());
        sort.accept(s);
        return this;
    }
}
