package cn.spider.framework.spider.param.engine.util;
import cn.spider.framework.db.util.RocksdbUtil;
import cn.spider.framework.spider.param.engine.metadata.convert.SpiderField;
import com.alibaba.fastjson.JSON;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableRocksdbUtil {
    private RocksdbUtil rocksdbUtil;

    private final String tableCName;


    public TableRocksdbUtil(RocksdbUtil rocksdbUtil) {
        this.rocksdbUtil = rocksdbUtil;
        this.tableCName = "spider_table";
    }

    /**
     * @param key   指的表名称
     * @param value 表结构信息
     * @throws Exception rocksdb的异常信息
     */
    public void putTable(String key, String value) throws Exception {
        rocksdbUtil.put(this.tableCName, key, value);
    }

    // 根据表名称,requestId查询数据返回json
    public JsonArray queryTable(String key, String requestId) throws Exception {
        String value = getTableLineBase(key, requestId);
        return new JsonArray(value);

    }

    /**
     * 插入或更新表格数据
     * 如果给定的key已经存在，则合并现有的数据和新的数据；如果不存在，则插入新的数据
     *
     * @param key 表格的唯一标识符
     * @param value 表格数据，以键值对形式存储
     * @param requestId 请求的唯一标识符，用于跟踪请求
     * @throws Exception 如果操作失败，抛出异常
     */
    public void insertTable(String key, Map<String, JsonObject> value, String requestId) throws Exception {
        // 尝试获取现有数据，以便进行合并
        String valueLine = getTableLineBase(key, requestId);
        if (StringUtils.isNotEmpty(valueLine)) {
            // 将现有数据解析为JSON对象
            JsonObject oldData = new JsonObject(valueLine);
            // 遍历新数据，进行合并
            for (String keys : value.keySet()) {
                // 如果现有数据中已存在该键，则合并数据
                if (oldData.containsKey(keys)) {
                    JsonObject insertValueNew = value.get(keys);
                    JsonObject insertValueOld = oldData.getJsonObject(keys);
                    // 遍历新数据的字段，更新到旧数据中
                    for (String insertKeys : insertValueNew.fieldNames()) {
                        insertValueOld.put(insertKeys, insertValueNew.getValue(insertKeys));
                    }
                } else {
                    // 如果现有数据中不存在该键，则直接添加
                    oldData.put(keys, value.get(keys));
                }
            }
            // 将合并后的数据存入数据库
            rocksdbUtil.put(requestId, key, oldData.toString());
            return;
        }
        // 如果没有现有数据，则直接将新数据存入数据库
        rocksdbUtil.put(requestId, key, JSON.toJSONString(value));
        // 发生事件到时间轮中
    }
    private String getTableLineBase(String key, String requestId) throws Exception {
        return rocksdbUtil.get(requestId, key);
    }

    /**
     * 获取table的行信息
     *
     * @param key 表结构信息,
     * @return 返回行信息
     * @throws Exception rocksdb的异常信息
     */
    public Map<String, JsonObject> getTableLine(String key, String requestId) throws Exception {
        String value = getTableLineBase(key, requestId);
        JsonObject jsonObject = new JsonObject(value);
        Map<String, JsonObject> result = new HashMap<>(jsonObject.size());
        for (String keys : jsonObject.fieldNames()) {
            result.put(keys, jsonObject.getJsonObject(keys));
        }
        return result;
    }
}
