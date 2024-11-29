package cn.spider.framework.domain.area.task.service;

import cn.spider.framework.domain.area.task.entity.SpiderTaskTestInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 测试用例的信息 服务类
 * </p>
 *
 * @author dds
 * @since 2024-11-10
 */
public interface ISpiderTaskTestInfoService extends IService<SpiderTaskTestInfo> {
    /**
     * 查询可以执行的任务
     */
    List<SpiderTaskTestInfo> queryCanRunInitTask();

    void runCase();
}
