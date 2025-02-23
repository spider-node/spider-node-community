package cn.spider.framework.spider.param.engine.metadata;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.spider.param.engine.metadata.convert.ConvertRow;
import cn.spider.framework.spider.param.engine.util.TableRocksdbUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

@Slf4j
public class Table {
    private ConvertRow convertRow;

    private TableRocksdbUtil tableRocksdbUtil;

    public Table(ConvertRow convertRow, TableRocksdbUtil tableRocksdbUtil) {
        this.convertRow = convertRow;
        this.tableRocksdbUtil = tableRocksdbUtil;
    }

    public void insert(String paramConfig, JsonObject data, String requestId) {
        Map<String, Map<String, JsonObject>> tableRowsInfo = convertRow.analysisDataToTable(paramConfig, data);
        tableRowsInfo.forEach((table, rows) -> {
            try {
                tableRocksdbUtil.insertTable(table, rows, requestId);
            } catch (Exception e) {
                log.error("插入数据失败 {}", ExceptionMessage.getStackTrace(e));
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 查询
     *
     * @param tableName 表名称
     * @param requestId 请求id
     * @return 表数据
     */
    public JsonArray query(String tableName, String requestId) {
        try {
            return tableRocksdbUtil.queryTable(tableName, requestId);
        } catch (Exception e) {
            log.error("查询数据失败 {}", ExceptionMessage.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量查询
     *
     * @param tableNames 表名称信息
     * @param requestId  请求id
     * @return 表名称对应结果
     */
    public Map<String, JsonArray> queryBatch(Set<String> tableNames, String requestId) {
        // 遍历tableName,多次查询，返回Map<String,JsonArray>,对应表名称与数据
        return tableNames
                .stream()
                .collect(java.util.stream.Collectors.toMap(tableName -> tableName, tableName -> query(tableName, requestId)));

    }
}
