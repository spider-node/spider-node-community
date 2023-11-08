package cn.spider.framework.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @program: flow-cloud
 * @description: 获取异常信息
 * @author: dds
 * @create: 2022-05-10 15:17
 */
public class ExceptionMessage {
    public static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }
}
