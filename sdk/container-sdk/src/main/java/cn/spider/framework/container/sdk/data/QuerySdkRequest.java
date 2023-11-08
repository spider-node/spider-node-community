package cn.spider.framework.container.sdk.data;

import cn.spider.framework.common.data.enums.JarStatus;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.container.sdk.data
 * @Author: dengdongsheng
 * @CreateTime: 2023-06-03  22:46
 * @Description: TODO
 * @Version: 1.0
 */
public class QuerySdkRequest {
    private String jarName;

    private JarStatus status;

    private Integer size;

    private Integer page;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public JarStatus getStatus() {
        return status;
    }

    public void setStatus(JarStatus status) {
        this.status = status;
    }
}
