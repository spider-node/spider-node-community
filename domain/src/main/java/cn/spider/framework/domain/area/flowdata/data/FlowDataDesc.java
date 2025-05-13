package cn.spider.framework.domain.area.flowdata.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

/*** @ClassName FlowDataDesc
 * @Description 流程中领域描述
 * @Author dds
 * @Date 2025/5/13 9:28
 */
@Data
public class FlowDataDesc {
    private Map<String, List<JSONObject>> flowDataDescMap;
}
