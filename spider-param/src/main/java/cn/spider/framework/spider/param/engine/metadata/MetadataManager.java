package cn.spider.framework.spider.param.engine.metadata;

import cn.spider.framework.db.util.RocksdbUtil;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

public class MetadataManager {
    private RocksdbUtil rocksdbUtil;

    private static final String METADATA_TABLE = "node_meta_data";

    // 下划线
    private static final String UNDERLINE = "_";

    public MetadataManager(RocksdbUtil rocksdbUtil) {
        this.rocksdbUtil = rocksdbUtil;
    }

    public void insert(String requestId, String nodeId, String value) throws Exception {
        String cName = METADATA_TABLE + UNDERLINE + requestId;
        rocksdbUtil.put(cName, nodeId, value);
    }

    public JsonObject query(String requestId, String nodeId) throws Exception {
        String cName = METADATA_TABLE + UNDERLINE + requestId;
        String value = rocksdbUtil.get(cName, nodeId);
        return StringUtils.isEmpty(value) ? new JsonObject() : new JsonObject(value);
    }
}
