package cn.spider.framework.spider.log.es.esx.model;

import org.noear.snack.ONode;

/**
 * @author noear
 * @since 1.0.14
 */
public class EsHighlightField {
    private final ONode oNode;

    public EsHighlightField(ONode oNode) {
        this.oNode = oNode;
    }


    public EsHighlightField preTags(String tags){
        oNode.set("pre_tags", tags);
        return this;
    }

    public EsHighlightField postTags(String tags){
        oNode.set("post_tags", tags);
        return this;
    }

    public EsHighlightField requireMatch(boolean requireMatch){
        oNode.set("require_field_match", requireMatch);
        return this;
    }
}
