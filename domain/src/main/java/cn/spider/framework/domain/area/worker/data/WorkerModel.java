package cn.spider.framework.domain.area.worker.data;

import lombok.Data;

import java.lang.ref.PhantomReference;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.domain.area.worker.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-09-09  14:15
 * @Description: 共组服务的基础属性
 * @Version: 1.0
 */
@Data
public class WorkerModel {
    /**
     * id
     */
    private String id;

    /**
     * 服务名称
     */
    private String workerName;

    /**
     * 描述
     */
    private String desc;

    /**
     * rpc端口
     */
    private Integer rpcPort;

    /**
     * 状态
     */
    private String status;
}
