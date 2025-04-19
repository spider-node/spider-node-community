package cn.spider.framework.domain.area.http.data;

import lombok.Data;

@Data
public class QueryHttpParam {
    private String httpFunctionName;

    private Long page;

    private Long size;
}
