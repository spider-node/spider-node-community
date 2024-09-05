package cn.spider.framework.linker.sdk.data;

import java.util.Set;

public class HostApplicationInfo {
    /**
     * 所有宿主机的ip
     */
    private Set<String> hostApplicationIp;

    /**
     * 该应用部署过的ip
     */
    private Set<String> deployAlready;

    public Set<String> getHostApplicationIp() {
        return hostApplicationIp;
    }

    public void setHostApplicationIp(Set<String> hostApplicationIp) {
        this.hostApplicationIp = hostApplicationIp;
    }

    public Set<String> getDeployAlready() {
        return deployAlready;
    }

    public void setDeployAlready(Set<String> deployAlready) {
        this.deployAlready = deployAlready;
    }
}
