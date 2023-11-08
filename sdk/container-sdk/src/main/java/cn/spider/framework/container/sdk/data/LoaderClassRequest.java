package cn.spider.framework.container.sdk.data;

import cn.spider.framework.common.data.enums.JarStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-28  01:12
 * @Description: 加载 jar包功能的请求参数类
 * @Version: 1.0
 */

public class LoaderClassRequest {

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public JarStatus getStatus() {
        return status;
    }

    public void setStatus(JarStatus status) {
        this.status = status;
    }
}
