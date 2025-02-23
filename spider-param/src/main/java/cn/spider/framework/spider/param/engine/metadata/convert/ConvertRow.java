package cn.spider.framework.spider.param.engine.metadata.convert;

import cn.spider.framework.spider.param.engine.datasource.ParamConfig;
import cn.spider.framework.spider.param.engine.enums.Type;
import com.alibaba.fastjson.JSON;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConvertRow {

    private final String fieldOriginalSource = "source";

    public List<ParamConfig> parseJson(String paramConfig) {
        // 解析json
        return JSON.parseArray(paramConfig, ParamConfig.class);
    }

    public Map<String, Map<String,JsonObject>> analysisDataToTable(String paramConfig, JsonObject data) {
        List<ParamConfig> paramConfigs = parseJson(paramConfig);
        Map<String,  Map<String,JsonObject>> tableRowsInfo = new HashMap<>();
        for (ParamConfig paramConfig1 : paramConfigs) {
            String fieldSource = paramConfig1.getSourceFiled();
            if (paramConfig1.getType().equals(Type.object)) {
                JsonObject source = fieldSource.equals(fieldOriginalSource) ? data : data.getJsonObject(fieldSource);
                // 当data中不包含的时候，直接跳过
                if(Objects.isNull(source)){
                    continue;
                }
                Map<String, String> fields = paramConfig1.getFields();
                JsonArray sourceRows = new JsonArray();
                sourceRows.add(source);
                JsonArray rows = buildRows(sourceRows, fields);
                tableRowsInfo.put(paramConfig1.getTable(), unique(rows, paramConfig1));
            } else {
                // 当data中不包含的时候，直接跳过
                if(!data.containsKey(fieldSource)){
                    continue;
                }
                JsonArray source = data.getJsonArray(fieldSource);
                Map<String, String> fields = paramConfig1.getFields();
                JsonArray rows = buildRows(source, fields);

                tableRowsInfo.put(paramConfig1.getTable(), unique(rows, paramConfig1));
            }
        }
        return tableRowsInfo;
    }

    private Map<String,JsonObject> unique(JsonArray rows, ParamConfig paramConfig1){
        Map<String,JsonObject> rowsMap = new HashMap<>();
        for(int i = 0; i < rows.size(); i++){
            JsonObject rowNew = rows.getJsonObject(i);
            if(rowNew.containsKey(paramConfig1.getUnique())){
                rowsMap.put(rowNew.getString(paramConfig1.getUnique()),rowNew);
            }
        }
        return rowsMap;
    }

    private JsonArray buildRows(JsonArray sourceRows, Map<String, String> fields) {
        if (sourceRows == null) {
            return null;
        }
        JsonArray rows = new JsonArray();
        // 遍历 sourceRow
        for (int i = 0; i < sourceRows.size(); i++) {
            JsonObject sourceRow = sourceRows.getJsonObject(i);
            JsonObject row = new JsonObject();
            fields.forEach((k, v) -> {
                if (!sourceRow.containsKey(k)) {
                    return;
                }
                row.put(v, sourceRow.getValue(k));
            });
            rows.add(row);
        }

        return rows;
    }
}
