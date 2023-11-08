package cn.spider.framework.spider.log.es.dao;
import cn.spider.framework.spider.log.es.domain.SpiderFlowElementExampleLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.dao
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-10  13:16
 * @Description: 配置dao
 * @Version: 1.0
 */
@Repository
public interface SpiderFlowElementExampleLogDao extends ElasticsearchRepository<SpiderFlowElementExampleLog,String> {
}
