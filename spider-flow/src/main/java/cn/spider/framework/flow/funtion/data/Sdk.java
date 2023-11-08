package cn.spider.framework.flow.funtion.data;

import cn.spider.framework.common.data.enums.JarStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.funtion.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  19:34
 * @Description: TODO
 * @Version: 1.0
 */
public class Sdk {
    private String id;

    private String jarName;

    private String classPath;

    private String url;

    private JarStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JarStatus getStatus() {
        return status;
    }

    public void setStatus(JarStatus status) {
        this.status = status;
    }
}
