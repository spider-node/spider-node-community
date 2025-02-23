package cn.spider.framework.domain.area.data;

import io.vertx.core.json.JsonArray;

import java.util.List;

public class UpdateBaseFieldParam {
    private Integer id;

    private String tableFieldInfos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableFieldInfos() {
        return tableFieldInfos;
    }

    public void setTableFieldInfos(String tableFieldInfos) {
        this.tableFieldInfos = tableFieldInfos;
    }
}
