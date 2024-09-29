package cn.spider.framework.domain.area.sondomain.service;

import cn.spider.framework.domain.area.sondomain.QuerySonAreaInfoParam;
import cn.spider.framework.domain.area.sondomain.entity.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 子域 服务类
 * </p>
 *
 * @author dds
 * @since 2024-09-20
 */
public interface ISpiderSonAreaService extends IService<SpiderSonArea> {
    QuerySonAreaResult querySonAreaBase(QuerySonAreaParam param);

    QuerySonAreaInfoResult querySonAreaInfos(QuerySonAreaInfoParam param);


    QuerySonAreaBaseResult querySonAreaBaseVersionInfo(QuerySonAreaBaseParam param);
}
