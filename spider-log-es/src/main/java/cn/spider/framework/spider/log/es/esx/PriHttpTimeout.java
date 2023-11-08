package cn.spider.framework.spider.log.es.esx;

/**
 * 超时：单位：秒
 *
 * @author noear
 * @since 1.7
 */
class PriHttpTimeout {
    public final int connectTimeout;
    public final int writeTimeout;
    public final int readTimeout;

    public PriHttpTimeout(int timeout) {
        this.connectTimeout = timeout;
        this.writeTimeout = timeout;
        this.readTimeout = timeout;
    }

    public PriHttpTimeout(int connectTimeout, int writeTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
        this.readTimeout = readTimeout;
    }
}
