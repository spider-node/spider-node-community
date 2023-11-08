package cn.spider.framework.spider.log.es.esx.model;

import org.noear.snack.ONode;

/**
 * @author noear
 * @since 1.2
 */
public class EsAliases {
    private final ONode oNode;
    public EsAliases(ONode oNode){
        this.oNode = oNode;
    }

    public EsAliases add(String indiceName, String alias) {
        oNode.addNew().getOrNew("add").set("index", indiceName).set("alias", alias);
        return this;
    }

    public EsAliases remove(String indiceName, String alias) {
        oNode.addNew().getOrNew("remove").set("index", indiceName).set("alias", alias);
        return this;
    }
}
