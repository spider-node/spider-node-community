package cn.spider.framework.spider.param.example;

import cn.spider.framework.db.map.RocksdbJson;
import cn.spider.framework.db.util.RocksdbUtil;
import org.rocksdb.RocksDBException;
import java.util.HashSet;
import java.util.Set;

/**
 * 参数实例
 */
public class ParamExample {
    private String exampleId;

    private RocksdbJson rocksdbJson;

    private Set<String> keys;

    private RocksdbUtil rocksdbUtil;

    public ParamExample(String exampleId, RocksdbUtil rocksdbUtil) {
        this.exampleId = exampleId;
        this.rocksdbJson = new RocksdbJson(rocksdbUtil, this.exampleId);
        this.keys = new HashSet<>();
    }

    public ParamExample(RocksdbUtil rocksdbUtil) {
        this.rocksdbUtil = rocksdbUtil;
    }

    public String getExampleId() {
        return exampleId;
    }

    public void setExampleId(String exampleId) {
        this.exampleId = exampleId;
    }

    /**
     * 插入数据
     * @param key
     * @param value
     */
    public void insert(String key, String value) {
        try {
            rocksdbJson.put(key, value);
            keys.add(key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取数据
     * @param key
     * @return
     */
    public String get(String key) {
        try {
            return rocksdbJson.get(key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除
     */
    public void remove() {
        keys.forEach(item->{
            rocksdbJson.remove(item);
        });
    }

    public void init(String exampleId){
        this.exampleId = exampleId;
        this.rocksdbJson = new RocksdbJson(rocksdbUtil, this.exampleId);
        this.keys = new HashSet<>();
    }
}
