package cn.spider.framework.domain.area.sondomain.service;

import cn.spider.framework.domain.area.sondomain.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * spider领域基础信息 服务类
 * </p>
 *
 * @author dds
 * @since 2024-09-20
 */
public interface IAreaDomainBaseInfoService extends IService<AreaDomainBaseInfo> {
    QuerySonAreaVersionResult querySonAreaVersion(QuerySonAreaVersionParam param);

    QuerySonAreaVersionResultV2 querySonAreaBaseV2(QuerySonAreaVersionParam param);
}
