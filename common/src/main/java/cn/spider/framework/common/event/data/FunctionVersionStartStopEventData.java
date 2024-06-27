package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FunctionVersionStartStopEventData {
    private String versionId;

    private String status;

    private String functionId;
}
