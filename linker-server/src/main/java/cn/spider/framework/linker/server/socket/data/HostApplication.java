package cn.spider.framework.linker.server.socket.data;

import cn.spider.framework.linker.server.socket.ClientInfo;

import java.util.Map;
import java.util.Set;

public class HostApplication {

    /**
     * 主机ip
     */
    private String ip;

    /**
     * rpc的访问
     */
    private ClientInfo clientInfo;

    /**
     * 功能集合 使用map是为了方便新增与移除
     */
    private Map<String,String> functionInfo;

    public Map<String, String> getFunctionInfo() {
        return functionInfo;
    }

    public void setFunctionInfo(Map<String, String> functionInfo) {
        this.functionInfo = functionInfo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }
}
