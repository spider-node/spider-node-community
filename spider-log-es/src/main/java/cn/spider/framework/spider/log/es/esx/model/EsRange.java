package cn.spider.framework.spider.log.es.esx.model;

import org.noear.snack.ONode;

/**
 * @author noear
 * @since 1.0
 */
public class EsRange {
    private final ONode oNode;
    public EsRange(ONode oNode){
        this.oNode = oNode;
    }

    public EsRange gt(Object value){
        oNode.set("gt",value);
        return this;
    }
    public EsRange gte(Object value){
        oNode.set("gte",value);
        return this;
    }

    public EsRange lt(Object value){
        oNode.set("lt",value);
        return this;
    }

    public EsRange lte(Object value){
        oNode.set("lte",value);
        return this;
    }
}
