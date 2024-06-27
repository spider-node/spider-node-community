package cn.spider.framework.common.event.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptChangeData extends EventData {
    private String brokerName;

    private Set<String> transcript;
}
