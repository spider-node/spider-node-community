package cn.spider.framework.domain.area.sondomain.service.impl;

import cn.spider.framework.domain.area.sondomain.entity.*;
import cn.spider.framework.domain.area.sondomain.mapper.AreaDomainBaseInfoMapper;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import cn.spider.framework.domain.area.util.ClassUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .eq(Objects.nonNull(param.getSonAreaId()),AreaDomainBaseInfo::getSonAreaId, param.getSonAreaId())
                .in(CollectionUtils.isNotEmpty(param.getSonAreaIds()),AreaDomainBaseInfo::getSonAreaId, param.getSonAreaIds())
                .in(CollectionUtils.isNotEmpty(param.getIds()),AreaDomainBaseInfo::getId, param.getIds())
                .list();
        return new QuerySonAreaVersionResult(areaDomainBaseInfoList);
    }

    @Override
    public QuerySonAreaVersionResultV2 querySonAreaBaseV2(QuerySonAreaVersionParam param) {
        List<AreaDomainBaseInfo> areaDomainBaseInfoList = lambdaQuery()
                .eq(Objects.nonNull(param.getSonAreaId()),AreaDomainBaseInfo::getSonAreaId, param.getSonAreaId())
                .in(CollectionUtils.isNotEmpty(param.getSonAreaIds()),AreaDomainBaseInfo::getSonAreaId, param.getSonAreaIds())
                .list();
        List<AreaDomainBaseInfoModel> baseInfoModels = areaDomainBaseInfoList.stream().map(item->{
            AreaDomainBaseInfoModel baseInfoModel = new AreaDomainBaseInfoModel();
            // 使用beanUtil进行把item copy到baseInfoModel
            BeanUtils.copyProperties(item, baseInfoModel);
            List<DomainFieldInfo> domainFieldInfos = JSONArray.parseArray(item.getDomainObject(), DomainFieldInfo.class);
            domainFieldInfos.forEach(items -> {
                items.setAreaFiled(ClassUtil.toLowerFirstChar(item.getDomainObjectEntityName()) + "." + items.getField());
            });
            baseInfoModel.setDomainFieldInfos(domainFieldInfos);
            return baseInfoModel;
        }).collect(Collectors.toList());
        return new QuerySonAreaVersionResultV2(baseInfoModels);
    }
}
