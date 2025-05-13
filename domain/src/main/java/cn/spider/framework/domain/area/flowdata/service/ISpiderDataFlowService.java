package cn.spider.framework.domain.area.flowdata.service;

import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataParam;
import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataResult;
import cn.spider.framework.domain.area.flowdata.data.UpdateFlowDescParam;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 数据流 服务类
 * </p>
 *
 * @author dds
 * @since 2024-09-19
 */
public interface ISpiderDataFlowService extends IService<SpiderDataFlow> {
    // 新增修改数据流
    void upsetFlowData(SpiderDataFlow flow);
    // 查询数据流
    QueryFlowDataResult querySpiderDataFlow(QueryFlowDataParam param);
    // 解析数据流
    Map<String,String> mateParamMapping();

    void updateFlowDataDesc(UpdateFlowDescParam param);
}
