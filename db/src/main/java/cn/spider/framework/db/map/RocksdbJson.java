package cn.spider.framework.db.map;
import cn.spider.framework.db.util.RocksdbUtil;
import org.apache.commons.lang3.StringUtils;
import org.rocksdb.RocksDBException;

public class RocksdbJson {

    private RocksdbUtil rocksdbUtil;

    // 会带着-broker-name的信息
    private String cfName;

    public RocksdbJson(RocksdbUtil rocksdbUtil, String cfName) {
        this.rocksdbUtil = rocksdbUtil;
        this.cfName = cfName;
    }

    /**
     * 获取数据
     *
     * @param key
     * @return
     * @throws RocksDBException
     */
    public String get(String key) throws RocksDBException {
        String object = rocksdbUtil.get(cfName, key);
        if (StringUtils.isEmpty(object)) {
            return null;
        }
        return object;
    }

    /**
     * 保持数据
     *
     * @param key
     * @param value
     * @throws RocksDBException
     */
    public void put(String key, String value) throws RocksDBException {
        rocksdbUtil.put(cfName, key, value);
    }

    /**
     * remove
     *
     * @param key key whose mapping is to be removed from the map
     * @return
     */
    public String remove(String key) {
        try {
            rocksdbUtil.delete(cfName, key);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
