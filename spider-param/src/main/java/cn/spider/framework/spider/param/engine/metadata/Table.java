package cn.spider.framework.spider.param.engine.metadata;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.spider.param.engine.util.TableRocksdbUtil;
import cn.spider.framework.spider.param.result.TableDataGenerator;
import cn.spider.framework.spider.param.result.TableFiledAnalysis;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Table {

    private TableRocksdbUtil tableRocksdbUtil;

    private TableDataGenerator tableDataGenerator;

    // 使用 guava cache
    private final String onlyKey = "id";


    public Table(TableRocksdbUtil tableRocksdbUtil) {
        this.tableRocksdbUtil = tableRocksdbUtil;
        this.tableDataGenerator = new TableDataGenerator();
    }

    /**
     * 把结果数据，放入table表数据信息
     *
     * @param paramConfig 参数的获取配置
     * @param data        结果数据
     * @param requestId   请求id
     */
    public void insert(JsonObject paramConfig, JsonObject data, String requestId) {
        TableFiledAnalysis analysisConfig = paramConfig.mapTo(TableFiledAnalysis.class);
        // 解析结果数据，转换成表格数据
        Map<String, List<Map<String, Object>>> result = tableDataGenerator.generate(data.getMap(), analysisConfig);
        result.forEach((table, rows) -> {
            try {
                Map<String, JsonObject> rowsMap = new HashMap<>();
                List<Map<String, Object>> rowsNew = rows;
                for (Map<String, Object> row : rowsNew) {
                    // 判断 row.get(onlyKey);类型是数字类型，或者string
                    if (!row.containsKey(onlyKey)) {

                    }
                    if (row.get(onlyKey) instanceof Integer || row.get(onlyKey) instanceof Long) {
                        rowsMap.put(row.get(onlyKey).toString(), new JsonObject(row));
                    } else if (row.get(onlyKey) instanceof String) {
                        rowsMap.put((String) row.get(onlyKey), new JsonObject(row));
                    } else {
                        throw new RuntimeException("onlyKey 类型错误");
                    }
                }
                tableRocksdbUtil.insertTable(table, rowsMap, requestId);
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
