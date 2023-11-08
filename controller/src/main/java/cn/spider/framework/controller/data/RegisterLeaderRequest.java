package cn.spider.framework.controller.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.controller.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-20  00:52
 * @Description: TODO
 * @Version: 1.0
 */

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class RegisterLeaderRequest {
    private String brokerName;

    private String brokerIp;
}
