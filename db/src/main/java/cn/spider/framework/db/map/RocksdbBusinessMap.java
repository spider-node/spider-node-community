package cn.spider.framework.db.map;

import cn.spider.framework.common.utils.CheckBaseClassUtil;
import cn.spider.framework.common.utils.ClassLoaderInterface;
import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.db.rocksdb.RocksdbKeyManager;
import cn.spider.framework.db.util.RocksdbUtil;
import com.alibaba.fastjson.JSON;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rocksdb.RocksDBException;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.map
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-06  14:51
 * @Description: 使用rocksdb进行存储
 * @Version: 1.0
 */
@Slf4j
public class RocksdbBusinessMap<k, v> extends HashMap {

    private RocksdbUtil rocksdbUtil;

    // 会带着-broker-name的信息
    private String cfName;

    private final String OBJECT_TYPE = "OBJECT_TYPE_NAME";

    private final String VALUE = "VALUE";

    private Map<String, Object> inScopeDataMap;

    private ClassLoaderInterface classLoaderInterface;

    private RocksdbKeyManager rocksdbKeyManager;
    //ColumnFamilyHandle

    public RocksdbBusinessMap(RocksdbUtil rocksdbUtil, String cfName, Map<String, ClassLoader> classLoaderMap,RocksdbKeyManager rocksdbKeyManager,ClassLoaderInterface classLoaderInterface) {
        this.rocksdbUtil = rocksdbUtil;
        this.cfName = cfName;
        this.inScopeDataMap = new HashMap<>();
        this.rocksdbKeyManager = rocksdbKeyManager;
        this.classLoaderInterface = classLoaderInterface;

    }


    public Object get(Object key) {
        try {
            String object = rocksdbUtil.get(cfName, (String) key);
            if (StringUtils.isEmpty(object)) {
                return null;
            }

            JsonObject jsonObject = new JsonObject(object);
            String className = jsonObject.getString(OBJECT_TYPE);
            // 判断类的对象路径为域对象的情况下，需要判断他是否为空
            if (className.equals("cn.spider.framework.flow.bus.InScopeData")) {
                if (jsonObject.getString(VALUE).equals("{}")) {
                    return this.inScopeDataMap.get(className);
                }
            }

            ClassLoader classLoader = classLoaderInterface.queryClassLoader(className);
            Class objectClass = Class.forName(className, false, classLoader);
            Object result = CheckBaseClassUtil.checkBaseClassType(className) ?
                    jsonObject.getString(VALUE) :
                    JSON.parseObject(jsonObject.getString(VALUE), objectClass);

            return result;
        } catch (Exception e) {
            log.error(ExceptionMessage.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * 直接写入rocksDb
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return
     */
    public Object put(Object key, Object value) {
        String className = value.getClass().getTypeName();
        if (className.equals("cn.spider.framework.flow.bus.InScopeData")) {
            if (JSON.toJSONString(value).equals("{}")) {
                log.info("对象为空 cn.spider.framework.flow.bus.InScopeData");
                this.inScopeDataMap.put(className, value);
            } else {
                this.inScopeDataMap.remove(className);
            }
        }
        JsonObject valueJson = new JsonObject()
                .put(OBJECT_TYPE, className)
                .put(VALUE, value instanceof String ? value : JSON.toJSONString(value));

        try {
            String keyNew = (String) key;
            rocksdbUtil.put(cfName, keyNew, valueJson.toString());
            rocksdbKeyManager.put(this.cfName,keyNew);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
        // 交给线程池区做
        return null;
    }

    /**
     * remove
     *
     * @param key key whose mapping is to be removed from the map
     * @return
     */
    public Object remove(Object key) {
        try {
            rocksdbUtil.delete(cfName, (String) key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 删除
     */
    public void clear() {
        try {
            rocksdbUtil.cfDeleteIfExist();
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }
}
