package cn.spider.framework.common.event.data;

public class ParamDelayDeleteData extends EventData  {
    private String key;

    public ParamDelayDeleteData(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
