package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopFunctionData extends EventData {
    private String functionId;
}
