package cn.spider.node.host.plugin.center.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 宿主应用
 * </p>
 *
 * @author dds
 * @since 2024-09-03
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("spider_host_application")
public class SpiderHostApplication {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * ip
     */
    private String ip;
}
