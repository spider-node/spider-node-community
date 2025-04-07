package cn.spider.framework.linker.sdk.data;

import java.util.Set;

public class QueryTaskDeployResult {
    private Set<String> ips;

    public QueryTaskDeployResult(Set<String> ips) {
        this.ips = ips;
    }

    public QueryTaskDeployResult() {
    }

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
    }
}
