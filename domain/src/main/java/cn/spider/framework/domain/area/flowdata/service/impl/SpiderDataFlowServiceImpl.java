package cn.spider.framework.domain.area.flowdata.service.impl;

import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataParam;
import cn.spider.framework.domain.area.flowdata.data.QueryFlowDataResult;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import cn.spider.framework.domain.area.flowdata.mapper.SpiderDataFlowMapper;
import cn.spider.framework.domain.area.flowdata.service.ISpiderDataFlowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 数据流 服务实现类
 * </p>
 *
 * @author dds
 * @since 2024-09-19
 */
@Service
public class SpiderDataFlowServiceImpl extends ServiceImpl<SpiderDataFlowMapper, SpiderDataFlow> implements ISpiderDataFlowService {


    @Override
    public void upsetFlowData(SpiderDataFlow flow) {
        saveOrUpdate(flow);
    }

    @Override
    public QueryFlowDataResult querySpiderDataFlow(QueryFlowDataParam param) {
        Page<SpiderDataFlow> rowPage = new Page(param.getPage(), param.getSize());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SpiderDataFlow>()
                .eq(StringUtils.isNotEmpty(param.getFlowDataName()), SpiderDataFlow::getFlowDataName, param.getFlowDataName());
        IPage page = baseMapper.selectPage(rowPage, queryWrapper);
        return new QueryFlowDataResult(rowPage.getRecords(), page.getTotal());
    }

    @Override
    public Map<String, String> mateParamMapping() {

        return null;
    }
}
