package cn.spider.framework.transaction.sdk.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @program: flow-cloud
 * @description:
 * @author: dds
 * @create: 2022-07-26 15:30
 */
public class FrameworkException extends RuntimeException {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkException.class);

    private static final long serialVersionUID = 5531074229174745826L;

    private final FrameworkErrorCode errcode;

    /**
     * Instantiates a new Framework exception.
     */
    public FrameworkException() {
        this(FrameworkErrorCode.UnknownAppError);
    }

    /**
     * Instantiates a new Framework exception.
     *
     * @param err the err
     */
    public FrameworkException(FrameworkErrorCode err) {
        this(err.getErrMessage(), err);
    }

    /**
     * Instantiates a new Framework exception.
     *
     * @param msg the msg
     */
    public FrameworkException(String msg) {
        this(msg, FrameworkErrorCode.UnknownAppError);
    }

    /**
     * Instantiates a new Framework exception.
     *
     * @param msg     the msg
     * @param errCode the err code
     */
    public FrameworkException(String msg, FrameworkErrorCode errCode) {
        this(null, msg, errCode);
    }

    /**
     * Instantiates a new Framework exception.
     *
     * @param cause   the cause
     * @param msg     the msg
     * @param errCode the err code
     */
    public FrameworkException(Throwable cause, String msg, FrameworkErrorCode errCode) {
        super(msg, cause);
        this.errcode = errCode;
    }

    /**
     * Instantiates a new Framework exception.
     *
     * @param th the th
     */
    public FrameworkException(Throwable th) {
        this(th, th.getMessage());
    }

    /**
     * Instantiates a new Framework exception.
     *
     * @param th  the th
     * @param msg the msg
     */
    public FrameworkException(Throwable th, String msg) {
        this(th, msg, FrameworkErrorCode.UnknownAppError);
    }

    /**
     * Gets errcode.
     *
     * @return the errcode
     */
    public FrameworkErrorCode getErrcode() {
        return errcode;
    }

    /**
     * Nested exception framework exception.
     *
     * @param e the e
     * @return the framework exception
     */
    public static FrameworkException nestedException(Throwable e) {
        return nestedException("", e);
    }

    /**
     * Nested exception framework exception.
     *
     * @param msg the msg
     * @param e   the e
     * @return the framework exception
     */
    public static FrameworkException nestedException(String msg, Throwable e) {
        LOGGER.error(msg, e.getMessage(), e);
        if (e instanceof FrameworkException) {
            return (FrameworkException) e;
        }

        return new FrameworkException(e, msg);
    }

    /**
     * Nested sql exception sql exception.
     *
     * @param e the e
     * @return the sql exception
     */
    public static SQLException nestedSQLException(Throwable e) {
        return nestedSQLException("", e);
    }

    /**
     * Nested sql exception sql exception.
     *
     * @param msg the msg
     * @param e   the e
     * @return the sql exception
     */
    public static SQLException nestedSQLException(String msg, Throwable e) {
        LOGGER.error(msg, e.getMessage(), e);
        if (e instanceof SQLException) {
            return (SQLException) e;
        }

        return new SQLException(e);
    }
}
