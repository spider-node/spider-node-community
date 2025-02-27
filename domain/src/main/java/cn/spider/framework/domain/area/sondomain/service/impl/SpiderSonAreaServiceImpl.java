package cn.spider.framework.domain.area.sondomain.service.impl;

import cn.spider.framework.domain.area.sondomain.entity.QuerySonAreaInfoParam;
import cn.spider.framework.domain.area.sondomain.entity.*;
import cn.spider.framework.domain.area.sondomain.mapper.SpiderSonAreaMapper;
import cn.spider.framework.domain.area.sondomain.service.IAreaDomainBaseInfoService;
import cn.spider.framework.domain.area.sondomain.service.ISpiderSonAreaService;
import cn.spider.framework.domain.area.util.ClassUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 子域 服务实现类
 * </p>
 *
 * @author dds
 * @since 2024-09-20
 */
@Service
public class SpiderSonAreaServiceImpl extends ServiceImpl<SpiderSonAreaMapper, SpiderSonArea> implements ISpiderSonAreaService {

    @Autowired
    private IAreaDomainBaseInfoService areaDomainBaseInfoService;

    @Override
    public QuerySonAreaResult querySonAreaBase(QuerySonAreaParam param) {
        Page<SpiderSonArea> rowPage = new Page(param.getPage(), param.getSize());
        rowPage.setSearchCount(true);
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SpiderSonArea>()
                .likeRight(StringUtils.isNotEmpty(param.getAreaName()), SpiderSonArea::getAreaName, param.getAreaName())
                .in(CollectionUtils.isNotEmpty(param.getIds()), SpiderSonArea::getId, param.getIds())
                .likeRight(StringUtils.isNotEmpty(param.getSonAreaName()), SpiderSonArea::getSonAreaName, param.getSonAreaName());
        super.baseMapper.selectPage(rowPage, queryWrapper);
        List<SpiderSonArea> spiderSonAreas = rowPage.getRecords();
        if (CollectionUtils.isEmpty(spiderSonAreas)) {
            return new QuerySonAreaResult(new ArrayList<>(), 0l);
        }
        List<String> sonAreaNames = spiderSonAreas.stream().map(SpiderSonArea::getSonAreaName).collect(Collectors.toList());
        List<AreaDomainBaseInfo> areaDomainBaseInfos = areaDomainBaseInfoService.lambdaQuery().in(AreaDomainBaseInfo::getSonAreaName, sonAreaNames)
                .orderByDesc(AreaDomainBaseInfo::getCreateTime)
                .list();
        if (CollectionUtils.isEmpty(areaDomainBaseInfos)) {
            return new QuerySonAreaResult(new ArrayList<>(), 0l);
        }

        Map<String, List<AreaDomainBaseInfo>> areaDomainBaseInfoMap = areaDomainBaseInfos
                .stream()
                .collect(Collectors.groupingBy(AreaDomainBaseInfo::getSonAreaName));
        List<QuerySonAreaData> sonAreaData = new ArrayList<>(spiderSonAreas.size());
        for (SpiderSonArea sonArea : spiderSonAreas) {
            List<AreaDomainBaseInfo> areaDomainBaseInfoList = areaDomainBaseInfoMap.get(sonArea.getSonAreaName());
            if (CollectionUtils.isEmpty(areaDomainBaseInfoList)) {
                continue;
            }
            // 把areaDomainBaseInfoList根据createTime进行排序返回创建时间最新的一条数据
            AreaDomainBaseInfo baseInfo = areaDomainBaseInfoList.stream().sorted(Comparator.comparing(AreaDomainBaseInfo::getId).reversed()).findFirst().get();

            List<DomainFieldInfo> domainFieldInfos = JSONArray.parseArray(baseInfo.getDomainObject(), DomainFieldInfo.class);
            domainFieldInfos.forEach(item -> {
                item.setAreaFiled(ClassUtil.toLowerFirstChar(baseInfo.getDomainObjectEntityName()) + "." + item.getField());
            });
            QuerySonAreaData areaData = new QuerySonAreaData(sonArea.getSonAreaName(), domainFieldInfos, sonArea.getId());
            sonAreaData.add(areaData);
        }
        return new QuerySonAreaResult(sonAreaData, rowPage.getTotal());
    }

    @Override
    public QuerySonAreaInfoResult querySonAreaInfos(QuerySonAreaInfoParam param) {
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SpiderSonArea>()
                .eq(Objects.nonNull(param.getId()), SpiderSonArea::getId, param.getId())
                .in(CollectionUtils.isNotEmpty(param.getIds()), SpiderSonArea::getId, param.getIds())
                .in(CollectionUtils.isNotEmpty(param.getAreaIds()), SpiderSonArea::getAreaId, param.getAreaIds())
                .likeRight(StringUtils.isNotEmpty(param.getTableName()), SpiderSonArea::getTableName, param.getTableName())
                .likeRight(StringUtils.isNotEmpty(param.getAreaName()), SpiderSonArea::getAreaName, param.getAreaName())
                .likeRight(StringUtils.isNotEmpty(param.getDatasource()), SpiderSonArea::getDatasource, param.getDatasource())
                .likeRight(StringUtils.isNotEmpty(param.getSonAreaName()), SpiderSonArea::getSonAreaName, param.getSonAreaName())
                .eq(StringUtils.isNotEmpty(param.getAreaId()), SpiderSonArea::getAreaId, param.getAreaId());
        Page<SpiderSonArea> rowPage = new Page(param.getPage(), param.getSize());
        super.baseMapper.selectPage(rowPage, queryWrapper);
        List<SpiderSonArea> spiderSonAreas = rowPage.getRecords();
        return new QuerySonAreaInfoResult(spiderSonAreas, rowPage.getTotal());
    }
}
