package cn.spider.framework.domain.area.task.entity.enums;

public enum AiCodeStep {
    // LOAD_DOMAIN_INFO/CODER/CHECK/CODER_ARRANGEMENT/COMPILE/COMPILE_ERROR/TEST/END
    LOAD_DOMAIN_INFO("加载项目信息"),
    CODER("生成代码"),
    CHECK("代码检查"),
    CODER_ARRANGEMENT("代码整理"),
    COMPILE("编译"),
    COMPILE_ERROR("编译错误"),
    TEST("测试"),
    END("结束");
    private String desc;
    AiCodeStep(String desc) {
        this.desc = desc;
    }
}
