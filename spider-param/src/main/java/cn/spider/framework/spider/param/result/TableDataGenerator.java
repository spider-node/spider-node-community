package cn.spider.framework.spider.param.result;

import cn.spider.framework.spider.param.enums.FiledType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public class TableDataGenerator {
    private ObjectMapper mapper;

    public TableDataGenerator() {
        this.mapper = new ObjectMapper();
    }

    // 核心解析方法
    public Map<String, List<Map<String, Object>>> generate(
            Map<String, Object> sourceData,
            TableFiledAnalysis analysisConfig) {

        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        // 遍历每个分析规则
        analysisConfig.getAnalysis().forEach(info -> {
            // 获取源数据（这里根据sourceFiled支持简单路径，可扩展为JSON Path）
            Object source = getNestedValue(sourceData, info.getSourceFiled());

            // 根据类型处理数据
            if (info.getType() == FiledType.object) {
                Map<String, Object> record = processObject(source, info.getFields());
                if (Objects.nonNull(record)) {
                    addToResult(result, info.getTable(), record);
                }
            } else if (info.getType() == FiledType.array) {
                processArray(source, info.getFields()).forEach(record ->
                        addToResult(result, info.getTable(), record));
            }
        });

        return result;
    }

    // 处理对象类型数据
    private Map<String, Object> processObject(Object source, Map<String, String> fieldMappings) {
        if (source == null) {
            return null;
        }
        Map<String, Object> sourceMap = convertToMap(source);
        Map<String, Object> resultMap = new HashMap<>();
        fieldMappings.forEach((key, value) -> {
            if (!sourceMap.containsKey(value)) {
                return;
            }
            resultMap.put(key, sourceMap.get(value));
        });
        return resultMap.isEmpty() ? null : resultMap;
    }

    // 处理数组类型数据
    private List<Map<String, Object>> processArray(Object source, Map<String, String> fieldMappings) {
        List<Object> sourceList = convertToList(source);
        return sourceList.stream()
                .map(item -> processObject(item, fieldMappings))
                .collect(Collectors.toList());
    }

    // 工具方法：嵌套值获取（支持简单的a.b.c格式路径）
    private Object getNestedValue(Map<String, Object> data, String path) {
        if (path.equals("source")) {
            return data;
        }
        String[] keys = path.split("\\.");
        Object current = data;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return current;
    }

    // 工具方法：类型安全转换
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object obj) {
        return (obj instanceof Map) ? (Map<String, Object>) obj : new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    private List<Object> convertToList(Object obj) {
        return (obj instanceof List) ? (List<Object>) obj : Collections.emptyList();
    }

    private void addToResult(Map<String, List<Map<String, Object>>> result,
                             String table, Map<String, Object> record) {
        result.computeIfAbsent(table, k -> new ArrayList<>()).add(record);
    }
}
