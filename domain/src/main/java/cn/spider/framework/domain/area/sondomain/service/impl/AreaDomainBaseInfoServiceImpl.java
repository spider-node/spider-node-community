package cn.spider.framework.domain.area.sondomain.service.impl;

import cn.spider.framework.domain.area.sondomain.entity.*;
import cn.spider.framework.domain.area.sondomain.mapper.AreaDomainBaseInfoMapper;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * spider领域基础信息 服务实现类
 * </p>
 *
 * @author dds
 * @since 2024-09-20
 */
@Service
public class AreaDomainBaseInfoServiceImpl extends ServiceImpl<AreaDomainBaseInfoMapper, AreaDomainBaseInfo> implements IAreaDomainBaseInfoService {


    @Override
    public QuerySonAreaVersionResult querySonAreaVersion(QuerySonAreaVersionParam param) {
        List<AreaDomainBaseInfo> areaDomainBaseInfoList = lambdaQuery()
                .eq(StringUtils.isNotEmpty(param.getSonAreaId()),AreaDomainBaseInfo::getSonAreaId, param.getSonAreaId())
                .list();
        return new QuerySonAreaVersionResult(areaDomainBaseInfoList);
    }
}
