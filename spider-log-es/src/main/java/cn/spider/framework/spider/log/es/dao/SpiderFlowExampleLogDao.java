package cn.spider.framework.spider.log.es.dao;
import cn.spider.framework.spider.log.es.domain.SpiderFlowExampleLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.dao
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-10  13:01
 * @Description: TODO
 * @Version: 1.0
 */
@Repository
public interface SpiderFlowExampleLogDao extends ElasticsearchRepository<SpiderFlowExampleLog,String> {
}
