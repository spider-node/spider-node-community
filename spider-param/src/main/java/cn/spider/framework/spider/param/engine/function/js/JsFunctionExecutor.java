package cn.spider.framework.spider.param.engine.function.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsFunctionExecutor {

    // 全局唯一 Context（启用多线程支持）
    private Context context;

    // 函数缓存：Key=函数名+代码哈希，Value=编译后的函数对象
    private Map<String, Value> functionCache;

    public JsFunctionExecutor() {
        this.context = Context.newBuilder("js")
                .allowAllAccess(true)
                .allowExperimentalOptions(true)
                .allowCreateThread(true) // 关键配置
                .build();
        this.functionCache = new ConcurrentHashMap<>();
    }

    public void loadFunction(String functionName, String jsCode) {
        if (functionCache.containsKey(functionName)) {
            return;
        }
        try {
            context.enter();
            // 执行JS代码，注册函数到全局作用域
            context.eval("js", jsCode);
            Value function = context.getBindings("js").getMember(functionName);
            if (function == null || !function.canExecute()) {
                throw new IllegalArgumentException("Function " + functionName + " not found or not executable");
            }
            functionCache.put(functionName, function);
        } catch (PolyglotException e) {
            throw new RuntimeException("Failed to load JS function: " + functionName, e);
        } finally {
            context.leave();

        }
    }

    public Object invokeFunction(String functionName, Map<String, Object> parameters) {
        try {
            context.enter();
            Value function = functionCache.get(functionName);
            if (function == null) {
                throw new IllegalArgumentException("Function not loaded: " + functionName);
            }
            // 转换参数为JS对象（深度转换）
            Value jsParams = convertJavaToJs(parameters, context);
            Value result = function.execute(jsParams);
            return convertValueToJava(result);
        } catch (PolyglotException e) {
            throw new RuntimeException("Error invoking JS function: " + functionName, e);
        } finally {
            context.leave();
        }
    }

    /**
     * 递归将Java对象转换为GraalVM JS可识别的原生类型
     * （确保Map/List在JS中是普通对象/数组而非Host对象）
     */
    private Value convertJavaToJs(Object javaObj, Context context) {
        if (javaObj == null) {
            return context.asValue(null);
        }
        // 处理Map -> JS对象
        if (javaObj instanceof Map) {
            Value jsObj = context.eval("js", "({})");
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) javaObj).entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                jsObj.putMember(key, convertJavaToJs(value, context));
            }
            return jsObj;
        }
        // 处理List/Set -> JS数组
        if (javaObj instanceof Collection) {
            Collection<?> coll = (Collection<?>) javaObj;
            Value jsArr = context.eval("js", "[]");
            int idx = 0;
            for (Object item : coll) {
                jsArr.setArrayElement(idx++, convertJavaToJs(item, context));
            }
            return jsArr;
        }
        // 处理数组 -> JS数组
        if (javaObj.getClass().isArray()) {
            int length = Array.getLength(javaObj);
            Value jsArr = context.eval("js", "[]");
            for (int i = 0; i < length; i++) {
                jsArr.setArrayElement(i, convertJavaToJs(Array.get(javaObj, i), context));
            }
            return jsArr;
        }
        // 处理Date -> JS Date
        if (javaObj instanceof Date) {
            return context.eval("js", "new Date(" + ((Date) javaObj).getTime() + ")");
        }
        // 其他类型交给GraalVM自动转换（String/Number/Boolean等）
        return context.asValue(javaObj);
    }

    private Object convertValueToJava(Value value) {
        if (value.isNull()) {
            return null;
        } else if (value.isString()) {
            return value.asString();
        } else if (value.isBoolean()) {
            return value.asBoolean();
        } else if (value.isNumber()) {
            if (value.fitsInInt()) {
                return value.asInt();
            } else if (value.fitsInLong()) {
                return value.asLong();
            } else {
                return value.asDouble();
            }
        } else if (value.hasArrayElements()) {
            List<Object> list = new ArrayList<>();
            long size = value.getArraySize();
            for (long i = 0; i < size; i++) {
                list.add(convertValueToJava(value.getArrayElement(i)));
            }
            return list;
        } else if (value.hasMembers()) {
            Map<String, Object> map = new HashMap<>();
            for (String key : value.getMemberKeys()) {
                map.put(key, convertValueToJava(value.getMember(key)));
            }
            return map;
        } else if (value.isHostObject()) {
            return value.asHostObject();
        } else {
            return value;
        }
    }
}