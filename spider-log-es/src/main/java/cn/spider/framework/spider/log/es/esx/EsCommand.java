package cn.spider.framework.spider.log.es.esx;

import java.io.Serializable;

/**
 * ElasticSearch 可执行命令
 *
 * @author noear
 * @since 1.0
 */
public class EsCommand implements Serializable {
    public String method;
    public String path;
    public String dsl;
    public String dslType;

    public transient PriHttpTimeout timeout;

    public EsCommand() {
        //设置默认值
        dslType = PriWw.mime_json;
    }
}
