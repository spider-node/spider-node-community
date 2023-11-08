package cn.spider.framework.db.map;

import cn.spider.framework.db.util.RocksdbUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.rocksdb.RocksDBException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.map
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-10  20:28
 * @Description:
 * @Version: 1.0
 */

public class RocksDbMap {

    // 会带着-broker-name的信息
    private String cfName;

    private final String VALUE = "VALUE";

    private RocksdbUtil rocksdbUtil;

    // 为了方便同步数据-》

    public RocksDbMap(String cfName, RocksdbUtil rocksdbUtil) {
        this.cfName = cfName;
        this.rocksdbUtil = rocksdbUtil;
    }

    public void put(String key, Object value) {
        JsonObject valueJson = new JsonObject()
                .put(VALUE, value instanceof String ? value : JSON.toJSONString(value));
        try {
            rocksdbUtil.put(cfName, key, valueJson.toString());
            // 里面是异步写入，固然不会影响性能
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        try {
            String value = rocksdbUtil.get(cfName, key);
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            JsonObject valueJson = new JsonObject(value);

            return JSON.parseObject(valueJson.getString(VALUE), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> getAll(Class<T> clazz) {
        try {
            Map<String, String> transactionAll = rocksdbUtil.getAll();
            List<T> objects = transactionAll.values().stream().map(item -> {
                JsonObject valueJson = new JsonObject(item);
                try {
                    System.out.println(valueJson.getString(VALUE));
                    JsonObject result = new JsonObject(valueJson.getString(VALUE));
                    return result.mapTo(clazz);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            return objects;
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAll() {
        try {
            Map<String, String> transactionAll = rocksdbUtil.getAll();
            return Lists.newArrayList(transactionAll.values());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

}
