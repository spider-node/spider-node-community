package cn.spider.framework.spider.param.result;
import cn.spider.framework.spider.param.enums.FiledType;
import java.util.Map;

public class AnalysisInfo {

    /**
     * 对象结果类中的字段
     */
    private String sourceFiled;

    /**
     * 表结构中与对象的字段取值
     */
    private Map<String,String> fields;

    /**
     * 字段的类型object/array
     */
    private FiledType type;

    /**
     * 对应表结构的信息
     */
    private String table;

    public String getSourceFiled() {
        return sourceFiled;
    }

    public void setSourceFiled(String sourceFiled) {
        this.sourceFiled = sourceFiled;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public FiledType getType() {
        return type;
    }

    public void setType(FiledType type) {
        this.type = type;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
