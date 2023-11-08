package cn.spider.framework.flow.resource.config;

import cn.spider.framework.flow.enums.ResourceTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.resource.config
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-26  15:37
 * @Description: TODO
 * @Version: 1.0
 */
public class AppointBpmnClassPatchConfigSource extends ClassPathConfigSource implements ConfigSource {

    public AppointBpmnClassPatchConfigSource() {
    }

    @Override
    public List<ConfigResource> getConfigResourceList() {

        return getResourceList().stream().map(BasicBpmnConfigResource::new).collect(Collectors.toList());
    }

    @Override
    public ResourceTypeEnum getResourceType() {
        return ResourceTypeEnum.APPOINT_BPMN;
    }

    public List<ConfigResource> getConfigResourceList(String fillName) {
        return getResourceList(fillName).stream().map(BasicBpmnConfigResource::new).collect(Collectors.toList());
    }
}
