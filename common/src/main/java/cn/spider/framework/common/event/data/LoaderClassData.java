package cn.spider.framework.common.event.data;

import cn.spider.framework.common.data.enums.JarStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class LoaderClassData extends EventData {
    private String jarName;

    private String classPath;

    private JarStatus status;

    private String url;

    private String id;
}
