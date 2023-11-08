package cn.spider.framework.spider.log.es.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.spider.log.es.client
 * @Author: dengdongsheng
 * @CreateTime: 2023-05-04  12:56
 * @Description: TODO
 * @Version: 1.0
 */
@ToString
@Data
@AllArgsConstructor
public class PageEsData<T> {
    /**
     * 响应信息
     */
    private List<T> data;

    /**
     * 总数据数
     */
    private long totalRows;

    /**
     * 获取记录开始行码
     *
     * @param pageNum  当前页数，从1开始
     * @param pageSize 每页显示数
     * @return 记录开始行码
     */
    public static int getDataStartNum(int pageNum, int pageSize) {
        return (pageNum - 1) * pageSize + 1;
    }

    /**
     * 初始化分页对象
     *
     * @param <T> 返回值类型
     * @return 初始化分页对象
     */
    public static <T> PageEsData<T> newPageData() {
        return new PageEsData<>(new ArrayList<>(), 0);
    }
}
