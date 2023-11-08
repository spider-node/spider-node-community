package cn.spider.framework.flow.funtion.data;

import cn.spider.framework.common.data.enums.BpmnStatus;
import io.vertx.sqlclient.templates.RowMapper;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  20:03
 * @Description: TODO
 * @Version: 1.0
 */
public class BpmnRow {
    public static RowMapper<Bpmn> ROW_BPMN = row -> {
        Bpmn bpmn = new Bpmn();
        bpmn.setId(row.getString("id"));
        bpmn.setBpmnName(row.getString("bpmn_name"));
        bpmn.setStatus(BpmnStatus.valueOf(row.getString("status")));
        bpmn.setUrl(row.getString("url"));
        return bpmn;
    };
}
