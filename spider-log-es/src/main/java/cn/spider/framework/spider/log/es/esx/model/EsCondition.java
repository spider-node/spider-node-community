package cn.spider.framework.spider.log.es.esx.model;
import cn.spider.framework.spider.log.es.esx.PriUtils;
import org.noear.snack.ONode;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * ElasticSearch 条件构建器
 *
 * @author noear
 * @since 1.0
 */
public class EsCondition {
    private final ONode oNode;

    public EsCondition(ONode oNode) {
        this.oNode = oNode;
    }

    ONode oNodeArray = null;
    String score_mode = null;


    /**
     * 设置过滤风格
     */
    private void filterStyleSet(String name) {
        if (score_mode == null) {
            oNodeArray = oNode.getOrNew("bool").getOrNew(name).asArray();
        } else {
            // 使用评分模式
            oNodeArray = oNode.getOrNew("function_score").getOrNew("query").getOrNew("bool").getOrNew(name).asArray();

            if (score_mode.length() > 0) {
                oNode.getOrNew("function_score").set("score_mode", score_mode);
            }
        }
    }

    /**
     * 设置过滤风格
     */
    private void filterSet(String type, String field, Object value) {
        if (oNodeArray == null) {
            if (score_mode == null) {
                oNode.getOrNew(type).set(field, value);
            } else {
                // 使用评分模式
                oNode.getOrNew("function_score").getOrNew("query").getOrNew(type).set(field, value);
                if (score_mode.length() > 0) {
                    oNode.getOrNew("function_score").set("score_mode", score_mode);
                }
            }
        } else {
            oNodeArray.add(PriUtils.newNode().build(n -> n.getOrNew(type).set(field, value)));
        }
    }

    /**
     * 启用评分定制
     * <p>
     * function_score/..
     */
    public EsCondition useScore() {
        return useScore(null);
    }

    /**
     * 启用评分定制
     * <p>
     * function_score/..
     */
    public EsCondition useScore(String mode) {
        if (mode == null) {
            score_mode = "";
        } else {
            score_mode = null;
        }

        return this;
    }


    /**
     * 只过滤，不参与打分
     * <p>
     * bool/filter
     */
    public EsCondition filter() {
        filterStyleSet("filter");
        return this;
    }

    /**
     * 如果有多个条件，这些条件都必须满足 and与
     * <p>
     * bool/must
     */
    public EsCondition must() {
        filterStyleSet("must");
        return this;
    }

    /**
     * 如果有多个条件，满足一个或多个即可 or或
     * <p>
     * bool/should
     */
    public EsCondition should() {
        filterStyleSet("should");
        return this;
    }

    /**
     * 设置should条件最小匹配数
     * <p>
     * bool/minimum_should_match
     */
    public EsCondition minimumShouldMatch(int min) {
        oNode.getOrNew("bool").set("minimum_should_match", min);
        return this;
    }

    /**
     * 和must相反，必须都不满足条件才可以匹配到 ！非
     * <p>
     * bool/mustNot
     */
    public EsCondition mustNot() {
        filterStyleSet("must_not");
        return this;
    }


    /**
     * match_all
     */
    public void matchAll() {
        oNode.getOrNew("match_all").asObject();
    }


    /**
     * nested（nested类型的内嵌对象）
     */
    public EsCondition nested(String path, Consumer<EsCondition> condition) {
        if (oNodeArray == null) {
            oNodeArray = PriUtils.newNode().asArray();
        }

        ONode oNode1 = PriUtils.newNode();
        EsCondition c = new EsCondition(oNode1);
        condition.accept(c);
        oNodeArray.addNew()
                .getOrNew("nested")
                .set("path", path)
                .set("query", oNode1);

        return this;
    }

    /**
     * match
     */
    public EsCondition match(String field, Object value) {
        filterSet("match", field, value);
        return this;
    }

    /**
     * match
     */
    public EsCondition matchIf(boolean condition, String field, Object value) {
        return condition ? match(field, value) : this;
    }

    /**
     * match_phrase
     */
    public EsCondition matchPhrase(String field, Object value) {
        filterSet("match_phrase", field, value);
        return this;
    }

    /**
     * match_phrase
     */
    public EsCondition matchPhraseIf(boolean condition, String field, Object value) {
        return condition ? matchPhrase(field, value) : this;
    }

    /**
     * match_phrase slop
     */
    public EsCondition matchPhrase(String field, Object value, int slop) {
        ONode oNode = PriUtils.newNode();
        oNode.set("query", value);
        oNode.set("slop", slop);

        filterSet("match_phrase", field, oNode);
        return this;
    }

    /**
     * match_phrase slop
     */
    public EsCondition matchPhraseIf(boolean condition, String field, Object value, int slop) {
        return condition ? matchPhrase(field, value, slop) : this;
    }

    /**
     * match_phrase_prefix
     */
    public EsCondition matchPhrasePrefix(String field, Object value) {
        filterSet("match_phrase_prefix", field, value);
        return this;
    }

    /**
     * match_phrase_prefix
     */
    public EsCondition matchPhrasePrefixIf(boolean condition, String field, Object value) {
        return condition ? matchPhrasePrefix(field, value) : this;
    }

    /**
     * match_phrase_prefix slop
     */
    public EsCondition matchPhrasePrefix(String field, Object value, int slop) {
        ONode oNode = PriUtils.newNode();
        oNode.set("query", value);
        oNode.set("slop", slop);

        filterSet("match_phrase_prefix", field, oNode);
        return this;
    }

    /**
     * match_phrase_prefix slop
     */
    public EsCondition matchPhrasePrefixIf(boolean condition, String field, Object value, int slop) {
        return condition ? matchPhrasePrefix(field, value, slop) : this;
    }

    /**
     * exists
     */
    public EsCondition exists(String field) {
        filterSet("exists", "field", field);
        return this;
    }

    /**
     * exists
     */
    public EsCondition existsIf(boolean condition, String field) {
        return condition ? exists(field) : this;
    }

    /**
     * term
     */
    public EsCondition term(String field, Object value) {
        filterSet("term", field, value);
        return this;
    }

    /**
     * term
     */
    public EsCondition termIf(boolean condition, String field, Object value) {
        return condition ? term(field, value) : this;
    }

    /**
     * terms
     */
    public EsCondition terms(String field, Object... values) {
        filterSet("terms", field, PriUtils.newNode().addAll(Arrays.asList(values)));
        return this;
    }

    /**
     * terms
     */
    public EsCondition terms(String field, Collection<?> values) {
        filterSet("terms", field, PriUtils.newNode().addAll(values));
        return this;
    }

    /**
     * terms
     */
    public EsCondition termsIf(boolean condition, String field, Object... values) {
        return condition ? terms(field, (Object[]) values) : this;
    }

    /**
     * terms
     */
    public EsCondition termsIf(boolean condition, String field, Collection<?> values) {
        return condition ? terms(field, values) : this;
    }

    /**
     * range
     */
    public EsCondition range(String field, Consumer<EsRange> range) {
        ONode oNode1 = PriUtils.newNode();
        EsRange r = new EsRange(oNode1);
        range.accept(r);

        filterSet("range", field, oNode1);
        return this;
    }

    /**
     * range
     */
    public EsCondition rangeIf(boolean condition, String field, Consumer<EsRange> range) {
        return condition ? range(field, range) : this;
    }


    /**
     * prefix
     */
    public EsCondition prefix(String field, String value) {
        filterSet("prefix", field, value);
        return this;
    }

    /**
     * prefix
     */
    public EsCondition prefixIf(boolean condition, String field, String value) {
        return condition ? prefix(field, value) : this;
    }

    /**
     * wildcard
     *
     * @param value *表示任意字符，?表示任意单个字符(
     */
    public EsCondition wildcard(String field, String value) {
        filterSet("wildcard", field, value);
        return this;
    }

    /**
     * wildcard
     *
     * @param value *表示任意字符，?表示任意单个字符(
     */
    public EsCondition wildcardIf(boolean condition, String field, String value) {
        return condition ? wildcard(field, value) : this;
    }

    /**
     * regexp
     */
    public EsCondition regexp(String field, String value) {
        filterSet("regexp", field, value);
        return this;
    }

    /**
     * regexp
     */
    public EsCondition regexpIf(boolean condition, String field, String value) {
        return condition ? regexp(field, value) : this;
    }

    /**
     * script
     */
    public EsCondition script(String source, Consumer<EsMap> params) {
        return script(source, "painless", params);
    }

    /**
     * script
     */
    public EsCondition script(String source, String lang, Consumer<EsMap> params) {
        EsMap p = new EsMap();
        params.accept(p);

        ONode oNode = PriUtils.newNode();
        oNode.set("source", source);
        oNode.set("lang", lang);
        if (p.size() > 0) {
            oNode.getOrNew("params").setAll(p);
        }

        filterSet("script", "script", oNode);
        return this;
    }


    /**
     * 添加下级条件
     */
    public EsCondition add(Consumer<EsCondition> condition) {
        if (oNodeArray == null) {
            throw new IllegalArgumentException("Conditions lack combination types");
        }

        EsCondition c = new EsCondition(oNodeArray.addNew());
        condition.accept(c);


        return this;
    }
}
