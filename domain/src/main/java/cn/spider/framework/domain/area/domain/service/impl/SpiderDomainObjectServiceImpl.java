package cn.spider.framework.domain.area.domain.service.impl;

import cn.spider.framework.domain.area.domain.entity.SpiderDomainObject;
import cn.spider.framework.domain.area.domain.mapper.SpiderDomainObjectMapper;
import cn.spider.framework.domain.area.domain.service.ISpiderDomainObjectService;
import cn.spider.framework.domain.area.flowdata.data.QueryDomainObjectParam;
import cn.spider.framework.domain.area.flowdata.data.QueryDomainObjectResult;
import cn.spider.framework.domain.area.flowdata.entity.SpiderDataFlow;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 领域对象 服务实现类
 * </p>
 *
 * @author dds
 * @since 2025-04-22
 */
@Service
public class SpiderDomainObjectServiceImpl extends ServiceImpl<SpiderDomainObjectMapper, SpiderDomainObject> implements ISpiderDomainObjectService {

    @Override
    public QueryDomainObjectResult queryDomainObjectParam(QueryDomainObjectParam param) {
        Page<SpiderDomainObject> rowPage = new Page(param.getPage(), param.getSize());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SpiderDomainObject>()
                .eq(StringUtils.isNotEmpty(param.getDomainObjectName()), SpiderDomainObject::getDomainObjectName, param.getDomainObjectName());
        IPage page = baseMapper.selectPage(rowPage, queryWrapper);
        return new QueryDomainObjectResult(page.getTotal(), page.getRecords());
    }
}
