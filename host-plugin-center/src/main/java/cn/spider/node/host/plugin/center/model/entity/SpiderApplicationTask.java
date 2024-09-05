package cn.spider.node.host.plugin.center.model.entity;

import cn.spider.node.host.plugin.center.model.entity.enums.TaskStatus;
import cn.spider.node.host.plugin.center.model.entity.enums.TaskType;
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
@TableName("spider_application_task")
public class SpiderApplicationTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 功能id
     */
    private Integer taskBusinessId;

    /**
     * 任务类型 INSTALL/UNINSTALL/DELETE_HOST
     */
    private TaskType taskType;

    /**
     * INIT/ING/SUSS/FAIL
     */
    private TaskStatus status;

    /**
     * 异常信息
     */
    private String error;

    /**
     * 宿主应用的ip
     */
    private String ip;
}
