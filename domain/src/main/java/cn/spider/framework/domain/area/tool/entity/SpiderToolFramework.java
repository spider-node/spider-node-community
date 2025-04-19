package cn.spider.framework.domain.area.tool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * spider工具框架
 * </p>
 *
 * @author dds
 * @since 2025-04-11
 */
@TableName("spider_tool_framework")
public class SpiderToolFramework implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 框架名称
     */
    private String frameworkName;

    /**
     * 框架描述
     */
    private String frameworkDesc;

    /**
     * 框架使用文档
     */
    private String frameworkDoc;

    /**
     * 使用框架引入的包名称
     */
    private String frameworkPackage;

    /**
     * 框架配置名称
     */
    private String frameworkConfigName;

    /**
     * 框架配置包名称
     */
    private String frameworkConfigPackage;

    /**
     * 框架的mvn依赖
     */
    private String frameworkMvn;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrameworkName() {
        return frameworkName;
    }

    public void setFrameworkName(String frameworkName) {
        this.frameworkName = frameworkName;
    }

    public String getFrameworkDesc() {
        return frameworkDesc;
    }

    public void setFrameworkDesc(String frameworkDesc) {
        this.frameworkDesc = frameworkDesc;
    }

    public String getFrameworkDoc() {
        return frameworkDoc;
    }

    public void setFrameworkDoc(String frameworkDoc) {
        this.frameworkDoc = frameworkDoc;
    }

    public String getFrameworkPackage() {
        return frameworkPackage;
    }

    public void setFrameworkPackage(String frameworkPackage) {
        this.frameworkPackage = frameworkPackage;
    }

    public String getFrameworkConfigName() {
        return frameworkConfigName;
    }

    public void setFrameworkConfigName(String frameworkConfigName) {
        this.frameworkConfigName = frameworkConfigName;
    }

    public String getFrameworkConfigPackage() {
        return frameworkConfigPackage;
    }

    public void setFrameworkConfigPackage(String frameworkConfigPackage) {
        this.frameworkConfigPackage = frameworkConfigPackage;
    }

    public String getFrameworkMvn() {
        return frameworkMvn;
    }

    public void setFrameworkMvn(String frameworkMvn) {
        this.frameworkMvn = frameworkMvn;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SpiderToolFramework{" +
            "id = " + id +
            ", frameworkName = " + frameworkName +
            ", frameworkDesc = " + frameworkDesc +
            ", frameworkDoc = " + frameworkDoc +
            ", frameworkPackage = " + frameworkPackage +
            ", frameworkConfigName = " + frameworkConfigName +
            ", frameworkConfigPackage = " + frameworkConfigPackage +
            ", frameworkMvn = " + frameworkMvn +
            ", createTime = " + createTime +
        "}";
    }
}
