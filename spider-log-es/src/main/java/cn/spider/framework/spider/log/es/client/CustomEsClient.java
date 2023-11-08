package cn.spider.framework.spider.log.es.client;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.client
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-04  12:52
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class CustomEsClient implements Closeable {
    /**
     * 默认超时时间
     */
    private long timeOut = 1L;

    /**
     * RestHighLevelClient
     */
    private RestHighLevelClient restHighLevelClient;

    public CustomEsClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * RestHighLevelClient
     *
     * @return 获取RestHighLevelClient实例
     */
    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    @Override
    public void close() throws IOException {
        restHighLevelClient.close();
    }

    /**
     * 设置超时时间
     *
     * @param timeOut 超时时间，单位：秒
     */
    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * 单条数据插入
     *
     * @param t 放入的对象
     * @return 响应信息，放入es的基本信息(index、type、id)
     */
    public IndexResponse add(EsIndexTypeId t) {
        if (checkIndexTypeId(t)) {
            return null;
        }
        String index = t.index();
        String type = t.type();
        String id = String.valueOf(t.id());
        final String jsonString = JSONObject.toJSONString(t);
        final IndexRequest request = new IndexRequest(index, type, id)
                .source(jsonString, XContentType.JSON)
                .opType(DocWriteRequest.OpType.CREATE)
                .timeout(TimeValue.timeValueSeconds(timeOut));
        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{},type:{},id:{})添加失败：", index, type, id, e);
        }
        return indexResponse;
    }

    /**
     * 批量数据插入
     * 记录是否有操作失败的数据，请调用（BulkResponse.hasFailures）
     *
     * @param ts 放入的对象集合
     * @return 响应信息，放入es的基本信息(index、type、id)
     */
    public BulkResponse upsertAll(List<EsIndexTypeId> ts) {
        EsIndexTypeId indexTypeId = ts.get(0);
        Map<Object,List<EsIndexTypeId>> dataMap = ts.stream().collect(Collectors.groupingBy(EsIndexTypeId :: id));
        Map<Object,Map<String,Object>> esIndexTypeIdMap = Maps.newHashMap();

        dataMap.forEach((key,value)->{
            List<EsIndexTypeId> datas = value;
            Map<String,Object> exampleMap = Maps.newHashMap();
            for(EsIndexTypeId esIndexTypeId : datas){
                JsonObject esIndexJson = JsonObject.mapFrom(esIndexTypeId);
                exampleMap.putAll(esIndexJson.getMap());
            }
            esIndexTypeIdMap.put(key,exampleMap);
        });
        return upsertAll0perate(esIndexTypeIdMap,indexTypeId.index(),indexTypeId.type());
    }


    public BulkResponse upsertAll0perate( Map<Object, Map<String,Object>> esIndexTypeIdMap,String index, String type) {
        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest request;
        for(Object key : esIndexTypeIdMap.keySet()){
            Map<String,Object> dataMap = esIndexTypeIdMap.get(key);
            final String jsonString = JSONObject.toJSONString(dataMap);
            request = new IndexRequest(index, type, String.valueOf(key))
                    .source(jsonString, XContentType.JSON).timeout(TimeValue.timeValueSeconds(timeOut));
            bulkRequest.add(request);
        }
        BulkResponse response = null;
        try {
            response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es批量添加失败：", e);
        }
        return response;
    }

    /**
     * 判断索引是否存在
     *
     * @param t 与es存储对应的对象
     * @return true存在，false不存在
     */
    public boolean existsIndex(EsIndexTypeId t) {
        return existsIndex(t.index());
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引
     * @return true存在，false不存在
     */
    public boolean existsIndex(String index) {
        if (index == null) {
            return false;
        }
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        request.masterNodeTimeout(TimeValue.timeValueSeconds(timeOut));
        boolean exists = false;
        try {
            exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{})查询异常：", index, e);
        }
        return exists;
    }

    public void createIndex(EsIndexTypeId t) {
        CreateIndexRequest request = new CreateIndexRequest(t.index());
        try {
            if(existsIndex(t.index())){
                return;
            }
            CreateIndexResponse createIndexResponse =
                    restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            log.info("[create index blog :{}]", acknowledged);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 删除单个数据
     *
     * @param t 与es存储对应的对象，需要对象的（index、type、id）
     * @return 删除对象的基本信息(index 、 type 、 id)
     */
    public DeleteResponse deleteById(EsIndexTypeId t) {
        if (checkIndexTypeId(t)) {
            return null;
        }
        DeleteRequest request = new DeleteRequest(t.index(), t.type(), String.valueOf(t.id()));
        //设置超时：等待主分片变得可用的时间
        request.timeout(TimeValue.timeValueMinutes(timeOut));

        //同步执行
        DeleteResponse deleteResponse = null;
        try {
            deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{},type:{},id:{})删除异常：", t.index(), t.type(), t.id(), e);
        }

        return deleteResponse;
    }

    /**
     * 检验必要的值
     *
     * @param t 与es存储对应的对象
     * @return true不符合，false符合
     */
    private boolean checkIndexTypeId(EsIndexTypeId t) {
        return t == null || t.index() == null || t.type() == null || t.id() == null;
    }

    /**
     * 删除索引
     *
     * @param t 与es存储对应的对象，需要对象的（index）
     * @return 删除索引的基本信息(index 、 type 、 id)
     */
    public AcknowledgedResponse deleteIndex(EsIndexTypeId t) {
        if (t == null || t.index() == null) {
            return null;
        }
        return deleteIndex(t.index());
    }

    /**
     * 删除索引
     *
     * @param index 索引
     * @return 删除索引的基本信息(index 、 type 、 id)
     */
    public AcknowledgedResponse deleteIndex(String index) {
        if (index == null) {
            return null;
        }
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        //设置超时：等待主分片变得可用的时间
        request.timeout(TimeValue.timeValueMinutes(timeOut));
        //同步执行
        AcknowledgedResponse acknowledgedResponse = null;
        try {
            acknowledgedResponse = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{})删除索引异常：", index, e);
        }

        return acknowledgedResponse;
    }

    /**
     * 根据ids批量删除指定信息
     * 记录是否有操作失败的数据，请调用（BulkResponse.hasFailures）
     *
     * @param index 索引
     * @param type  索引指定的类型
     * @param ids   要删除的Id
     * @return 删除后响应信息
     */
    public BulkResponse deleteByIds(String index, String type, List<Long> ids) {
        if (index == null || type == null || ids == null || ids.isEmpty()) {
            return null;
        }

        final BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueSeconds(timeOut));
        DeleteRequest deleteRequest;
        for (Long id : ids) {
            deleteRequest = new DeleteRequest();
            deleteRequest.index(index);
            deleteRequest.type(type);
            deleteRequest.id(id + "");
            request.add(deleteRequest);
        }

        BulkResponse response = null;
        try {
            response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{},type:{},ids:{})删除索引异常：", index, type, ids.toString(), e);
        }
        return response;
    }

    /**
     * 根据不同的索引和类型和id批量删除指定信息
     * 记录是否有操作失败的数据，请调用（BulkResponse.hasFailures）
     *
     * @param ts 与es存储对应的对象
     * @return 删除后响应信息
     */
    public BulkResponse deleteByIndexAndTypeAndIds(List<EsIndexTypeId> ts) {
        final BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueSeconds(timeOut));
        DeleteRequest deleteRequest;
        for (EsIndexTypeId t : ts) {
            if (checkIndexTypeId(t)) {
                continue;
            }
            deleteRequest = new DeleteRequest();
            deleteRequest.index(t.index());
            deleteRequest.type(t.type());
            deleteRequest.id(t.id() + "");
            request.add(deleteRequest);
        }

        BulkResponse response = null;
        try {
            response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es根据不同类型的索引删除数据异常：", e);
        }
        return response;
    }

    /**
     * 修改单条数据
     *
     * @param t 与es存储对应的对象
     * @return 响应修改对象的基本信息(index 、 type 、 id)
     */
    public UpdateResponse update(EsIndexTypeId t) {
        if (checkIndexTypeId(t)) {
            return null;
        }
        UpdateRequest request = new UpdateRequest(t.index(), t.type(), String.valueOf(t.id()));
        String jsonString = JSONObject.toJSONString(t);
        request.upsert(jsonString, XContentType.JSON);

        //设置超时：等待主分片变得可用的时间
        request.timeout(TimeValue.timeValueSeconds(timeOut));
        // 刷新
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        //如果要更新的文档在获取或者索引阶段已被另一操作更改，则重试更新操作的次数
        request.retryOnConflict(3);
        // 禁用noop检测
        request.detectNoop(false);
        // 无论文档是否存在，脚本都必须运行，即如果脚本尚不存在，则脚本负责创建文档。
        request.scriptedUpsert(false);
        // 如果不存在，则表明部分文档必须用作upsert文档。
        request.docAsUpsert(false);
        request.doc(jsonString, XContentType.JSON);
        //设置在继续更新操作之前必须激活的分片副本的数量。
//        request.waitForActiveShards(2);
        //使用ActiveShardCount方式，可以是ActiveShardCount.ALL，ActiveShardCount.ONE或ActiveShardCount.DEFAULT（默认值）
//        request.waitForActiveShards(ActiveShardCount.ALL);

        //同步执行
        UpdateResponse updateResponse = null;
        try {
            updateResponse = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{},type:{},id:{})更新异常：", t.index(), t.type(), t.id(), e);
        }
        return updateResponse;
    }

    /**
     * 根据Id查询数据
     *
     * @param t      与es存储对应的对象
     * @param tClass 与es存储对应的对象的类型
     * @return 响应信息
     */
    public <T> T queryById(EsIndexTypeId t, Class<T> tClass) {
        // 检查参数
        if (checkIndexTypeId(t)) {
            return null;
        }

        // 根据ID查询数据
        final GetRequest getRequest = new GetRequest().index(t.index()).type(t.type()).id(String.valueOf(t.id()));
        GetResponse documentFields = null;
        try {
            documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{},type:{},id:{})根据文件Id查询异常：", t.index(), t.type(), t.id(), e);
        }

        if (documentFields == null) {
            return null;
        }
        return JSONObject.parseObject(documentFields.getSourceAsString(), tClass);
    }

    /**
     * 查询数据，带分页，且不指定分页启用默认值
     *
     * @param sourceBuilder 查询条件
     * @param index         索引
     * @param type          索引类型
     * @param tClass        返回对象类型
     * @param <T>           返回对象类型
     * @return 分页数据
     */
    public <T> PageEsData<T> searchPage(SearchSourceBuilder sourceBuilder, String index, String type, Class<T> tClass) {
        // 校验参数
        if (sourceBuilder == null) {
            return PageEsData.newPageData();
        }

        // 默认第0条开始
        if (sourceBuilder.from() < 0) {
            sourceBuilder.from(0);
        }
        // 默认一页展示10条
        if (sourceBuilder.size() < 1) {
            sourceBuilder.size(10);
        }

        SearchResponse search = null;
        try {
            final SearchRequest request = new SearchRequest(index);
            if (type != null) {
                request.types(type);
            }
            sourceBuilder.timeout(TimeValue.timeValueSeconds(timeOut));
            request.source(sourceBuilder);

            search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es(index:{},type:{})根据条件查询异常：", index, type, e);
        }

        if (search == null) {
            return null;
        }

        if (RestStatus.OK.getStatus() != search.status().getStatus()) {
            log.warn("es(index:{},type:{})根据条件查询失败（状态码：{}）", index, type, search.status().getStatus());
            return PageEsData.newPageData();
        }

        // 响应数据
        final SearchHits hits = search.getHits();
        if (hits == null) {
            return PageEsData.newPageData();
        }

        return new PageEsData<>(Arrays.stream(hits.getHits()).filter(Objects::nonNull)
                .map(documentFields -> JSONObject.parseObject(documentFields.getSourceAsString(), tClass))
                .collect(Collectors.toList()), hits.getTotalHits());
    }
}
