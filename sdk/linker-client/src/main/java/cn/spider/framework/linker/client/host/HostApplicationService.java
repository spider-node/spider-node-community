package cn.spider.framework.linker.client.host;

import cn.spider.framework.linker.sdk.data.LinkerServerRequest;

public interface HostApplicationService {
    Object runFunction(LinkerServerRequest request);
}
