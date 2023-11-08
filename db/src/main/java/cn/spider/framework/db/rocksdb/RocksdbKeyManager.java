package cn.spider.framework.db.rocksdb;

import java.util.*;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.rocksdb
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-23  02:44
 * @Description: TODO
 * @Version: 1.0
 */
public class RocksdbKeyManager {
    private Map<String, List<String>> requestIdParamMap;

    public RocksdbKeyManager() {
        this.requestIdParamMap = new HashMap<>();
    }

    public void put(String key, String value) {
        if (!requestIdParamMap.containsKey(key)) {
            requestIdParamMap.put(key, new ArrayList<>());
        }
        requestIdParamMap.get(key).add(value);
    }

    public void delete(String key){
        if(!this.requestIdParamMap.containsKey(key)){
            return;
        }
        requestIdParamMap.remove(key);
    }

    public List<String> query(String key){
        return this.requestIdParamMap.get(key);
    }

}
