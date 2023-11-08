/*
 *
 *  * Copyright (c) 2020-2023, Lykan (jiashuomeng@gmail.com).
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package cn.spider.framework.flow.resource.config;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cn.spider.framework.flow.enums.ResourceTypeEnum;

/**
 * 类路径配资资源：属性变量
 *
 * @author lykan
 */
public class PropertiesClassPathConfigSource extends ClassPathConfigSource implements ConfigSource {

    private final List<String> YAML_SUFFIX_NAME_LIST = Lists.newArrayList(".yaml", ".yml");

    public PropertiesClassPathConfigSource() {
    }

    @Override
    public List<ConfigResource> getConfigResourceList() {
        return getResourceList().stream()
                .filter(resource -> YAML_SUFFIX_NAME_LIST.stream().anyMatch(resource.getFilename()::endsWith))
                .map(BasicPropertiesConfigResource::new).collect(Collectors.toList());
    }

    @Override
    public ResourceTypeEnum getResourceType() {
        return ResourceTypeEnum.PROPERTIES;
    }

    @Override
    public List<ConfigResource> getConfigResourceList(String fillName) {
        return null;
    }
}
