package cn.spider.framework.db.util;

import cn.spider.framework.common.utils.ExceptionMessage;
import cn.spider.framework.db.config.RocksDbConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.rocksdb.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.db.map
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-06  04:55
 * @Description: 继承hashmap
 * @Version: 1.0
 */
@Slf4j
public class RocksdbUtil {

    private static RocksDB rocksDB;

    private static final String COLUMN_FAMILY_HANDLE = "COLUMN_FAMILY_HANDLE";

    private static RocksdbUtil rocksdbUtil;

    private static ColumnFamilyHandle columnFamilyHandle;

    public static RocksdbUtil getInstance() {
        if (Objects.isNull(rocksdbUtil)) {
            rocksdbUtil = new RocksdbUtil();
        }
        return rocksdbUtil;
    }

    private RocksdbUtil() {
        try {
            String osName = System.getProperty("os.name");
            log.info("osName:{}", osName);
            String rocksDBPath = null;
            if (osName.toLowerCase().contains("windows")) {
                rocksDBPath = "D:\\RocksDB-spider\\"; // 指定windows系统下RocksDB文件目录
            } else {
                // linux下的路径
                rocksDBPath = "/usr/local/rocksdb/";
            }
            RocksDB.loadLibrary();
            Options options = new Options();
            options.setCreateIfMissing(true); //如果数据库不存在则创建
            List<byte[]> cfArr = RocksDB.listColumnFamilies(options, rocksDBPath); // 初始化所有已存在列族
            List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>(); //ColumnFamilyDescriptor集合
            if (!ObjectUtils.isEmpty(cfArr)) {
                for (byte[] cf : cfArr) {
                    columnFamilyDescriptors.add(new ColumnFamilyDescriptor(cf, new ColumnFamilyOptions()));
                }
            } else {
                columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));
            }
            RocksDbConfiguration rocksDbConfiguration = new RocksDbConfiguration();

            DBOptions dbOptions = new DBOptions()
                    .setErrorIfExists(false)
                    .setCreateIfMissing(true)
                    .setParanoidChecks(true)
                    .setMaxOpenFiles(rocksDbConfiguration.getMaxOpenFiles())
                    // 1 flush, 1 compaction
                    .setMaxBackgroundJobs(2)
                    // we only use the default CF
                    .setCreateMissingColumnFamilies(false)
                    // may not be necessary when WAL is disabled, but nevertheless recommended to avoid
                    // many small SST files
                    .setAvoidFlushDuringRecovery(true)
                    // limit the size of the manifest (logs all operations), otherwise it will grow
                    // unbounded
                    .setMaxManifestFileSize(64 * 1024 * 1024L)
                    // keep 1 hour of logs - completely arbitrary. we should keep what we think would be
                    // a good balance between useful for performance and small for replication
                    .setLogFileTimeToRoll(1000 * 60 * 30)
                    .setKeepLogFileNum(2);

            WriteBufferManager writeBufferManager = new WriteBufferManager(600 * 1024 * 1024,new LRUCache(64 * 1024 * 1024),true);
            dbOptions.setWriteBufferManager(writeBufferManager);
            List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>(); //ColumnFamilyHandle集合
            rocksDB = RocksDB.open(dbOptions, rocksDBPath, columnFamilyDescriptors, columnFamilyHandles);
            log.info("RocksDB init success!! path:{}", rocksDBPath);
            ColumnFamilyOptions columnFamilyOptions = new ColumnFamilyOptions();
            columnFamilyOptions.setWriteBufferSize(128 * 1024 *1024);
            columnFamilyOptions.setMinWriteBufferNumberToMerge(2);
            columnFamilyOptions.setMaxWriteBufferNumber(4);

            columnFamilyHandle = rocksDB.createColumnFamily(new ColumnFamilyDescriptor(COLUMN_FAMILY_HANDLE.getBytes(), columnFamilyOptions));
        } catch (Exception e) {
            log.error("RocksDB init failure!! error:{}", e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 列族，删除（如果存在）
     */
    public void cfDeleteIfExist() throws RocksDBException {
        rocksDB.dropColumnFamily(columnFamilyHandle);
    }
    /**
     * 增
     */
    public void put(String cfName, String key, String value) throws RocksDBException {
        String keyNew = cfName + key;
        rocksDB.put(columnFamilyHandle, keyNew.getBytes(), value.getBytes());
    }

    /**
     * 删
     */
    public void delete(String cfName, String key) throws RocksDBException {
        String keyNew = cfName + key;
         //获取列族Handle
        rocksDB.delete(columnFamilyHandle, keyNew.getBytes());
    }

    /**
     * 查
     */
    public String get(String cfName, String key) throws RocksDBException {
        String value = null;
        String keyNew = cfName + key;
        byte[] bytes = rocksDB.get(columnFamilyHandle, keyNew.getBytes());
        if (!ObjectUtils.isEmpty(bytes)) {
            value = new String(bytes, StandardCharsets.UTF_8);
        }
        return value;
    }

    /**
     * 查（所有键值）
     */
    public Map<String, String> getAll() throws RocksDBException {
        Map<String, String> map = new HashMap<>();
        try (RocksIterator rocksIterator = rocksDB.newIterator(columnFamilyHandle)) {
            for (rocksIterator.seekToFirst(); rocksIterator.isValid(); rocksIterator.next()) {
                map.put(new String(rocksIterator.key(), StandardCharsets.UTF_8), new String(rocksIterator.value(), StandardCharsets.UTF_8));
            }
        }
        return map;
    }
}
