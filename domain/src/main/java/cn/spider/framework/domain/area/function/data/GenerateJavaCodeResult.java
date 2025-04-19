package cn.spider.framework.domain.area.function.data;

import cn.spider.framework.domain.area.function.enums.GenerateCoderType;
import cn.spider.framework.domain.area.function.version.enums.ToJavaEntitySource;
import lombok.Data;

import java.util.List;

@Data
public class GenerateJavaCodeResult {
    private String functionVersionId;

    private GenerateCoderType generateCoderType;

    private List<String> codes;

    private ToJavaEntitySource source;

    private Integer httpFunctionId;
}
