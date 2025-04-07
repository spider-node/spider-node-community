package cn.spider.framework.spider.param.engine.enums;

public enum JsRunTimeErrorType {
    LOAD_ERROR("加载错误"),

    JS_RUN_ERROR("js运行错误");

    private String errorMsg;

    JsRunTimeErrorType(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
