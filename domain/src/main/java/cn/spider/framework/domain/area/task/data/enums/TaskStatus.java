package cn.spider.framework.domain.area.task.data.enums;

public enum TaskStatus {
    // /DATA_INIT(数据准备)/CODING(编码钟)/COMPILE(编译)/TEST_DATA_INIT(测试数据准备)/TEST(测试)/FINISH(完成)
    DATA_INIT("DATA_INIT"),
    CODING("CODING"),
    COMPILE("COMPILE"),
    TEST_DATA_INIT("TEST_DATA_INIT"),
    TEST("TEST"),
    FINISH("FINISH"),
    ERROR("ERROR"),
    ;

    private String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
