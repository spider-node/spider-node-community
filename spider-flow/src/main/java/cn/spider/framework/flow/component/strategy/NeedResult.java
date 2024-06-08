package cn.spider.framework.flow.component.strategy;

public class NeedResult {
    private Boolean need;

    private String id;

    public NeedResult(Boolean need, String id) {
        this.need = need;
        this.id = id;
    }

    public Boolean getNeed() {
        return need;
    }

    public void setNeed(Boolean need) {
        this.need = need;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
