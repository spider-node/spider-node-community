package cn.spider.framework.spider.log.es.esx.exception;

/**
 * 不存在异常
 *
 * @author noear
 * @since 1.4
 */
public class NoExistException extends RuntimeException {
    public NoExistException(String message) {
        super(message);
    }
}
