package cn.spider.framework.spider.log.es.esx;
/**
 * @author noear 2022/4/25 created
 */
public class EsCommandHolder {
    private final EsContext context;
    private final EsCommand command;

    private long timespan;

    public EsCommandHolder (EsContext context, EsCommand command){
        this.context = context;
        this.command = command;
    }

    public void setTimespan(long timespan) {
        this.timespan = timespan;
    }

    public long getTimespan() {
        return timespan;
    }

    public EsContext getContext() {
        return context;
    }

    public String getMethod() {
        return command.method;
    }

    public String getPath() {
        return command.path;
    }

    public String getDsl() {
        return command.dsl;
    }

    public String getDslType() {
        return command.dslType;
    }
}
