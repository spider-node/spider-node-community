package cn.spider.framework.domain.area.function.version.data;

import cn.spider.framework.domain.area.function.version.enums.ToJavaEntitySource;
import lombok.Data;

@Data
public class ToJavaParam {
    private String functionVersionId;

    private ToJavaEntitySource source;

    private Integer httpFunctionId;
}
