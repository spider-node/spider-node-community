package cn.spider.framework.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnowIdDto implements Serializable, Comparable<SnowIdDto> {

    /**
     * 注册时的时间戳
     */
    private Long timestamp;

    /**
     * 数据中心节点  0~31
     */
    private Integer dataCenterId;
    /**
     * 工作节点 0~31
     */
    private Integer workerId;

    @Override
    public int compareTo(SnowIdDto o) {
        long ex = this.timestamp - o.getTimestamp();
        return ex > 0 ? 1 : -1;
    }
}

