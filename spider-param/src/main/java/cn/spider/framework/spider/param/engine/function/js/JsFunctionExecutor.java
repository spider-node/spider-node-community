package cn.spider.framework.spider.param.engine.function.js;

import cn.spider.framework.spider.param.config.Constants;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsFunctionExecutor {

    private Map<String, Context> contextMap;

    private Map<String, Map<String, Value>> threadFunctionCache;

    public JsFunctionExecutor() {
        this.contextMap = new ConcurrentHashMap<>();
        this.threadFunctionCache = new ConcurrentHashMap<>();

    }

    /**
     * 通知functionName 失效
     *
     * @param functionName js 函数名称
     */
    public void functionJsLose(String functionName) {
        this.threadFunctionCache.forEach((k, v) -> {
            v.remove(functionName);
        });
    }

    public void loadFunction(String functionName, String jsCode, String threadName) {
        if (jsCode == null) {
            throw new IllegalArgumentException("JS code cannot be null");
        }
        if (!contextMap.containsKey(threadName)) {
            Context context = Context.newBuilder("js")
                    .allowAllAccess(true)
                    .allowExperimentalOptions(true)
                    .allowCreateThread(true) // 关键配置
                    .build();
            contextMap.put(threadName, context);
        }
        Context context = contextMap.get(threadName);
        Map<String, Value> functionCache = threadFunctionCache.computeIfAbsent(threadName, k -> new ConcurrentHashMap<>());
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

    public void unloadFunction(String functionName, String threadName) {
        Map<String, Value> functionCache = threadFunctionCache.get(threadName);
        if (functionCache != null) {
            functionCache.remove(functionName);
        }
        // 可以考虑在JS上下文中删除函数定义
        Context context = contextMap.get(threadName);
        if (context != null) {
            try {
                context.enter();
                context.getBindings("js").removeMember(functionName);
            } finally {
                context.leave();
            }
        }
    }

    public Object invokeFunction(String functionName, Map<String, Object> parameters, String threadName) {
        Context context = contextMap.get(threadName);
        try {
            context.enter();
            Map<String, Value> functionCache = threadFunctionCache.get(threadName);
            Value function = functionCache.get(functionName);

            if (function == null) {
                throw new IllegalArgumentException("Function not loaded: " + functionName);
            }
            Value result = null;
            if (parameters.containsKey(Constants.CONTEXT_KEY) && !parameters.containsKey(Constants.REQUEST_KEY)) {
                result = runContext(function, convertJavaToJS(context, parameters.get(Constants.CONTEXT_KEY)));
            } else if (parameters.containsKey(Constants.REQUEST_KEY) && !parameters.containsKey(Constants.CONTEXT_KEY)) {
                result = runRequest(function, convertJavaToJS(context, parameters.get(Constants.REQUEST_KEY)));
            } else {
                result = runContextAndRequest(function, convertJavaToJS(context, parameters.get(Constants.CONTEXT_KEY)), convertJavaToJS(context, parameters.get(Constants.REQUEST_KEY)));
            }
            return convertValueToJava(result);
        } catch (PolyglotException e) {
            throw new RuntimeException("Error invoking JS function: " + functionName, e);
        } finally {
            context.leave();
        }
    }


    private Value runContext(Value function, Value context) {
        return function.execute(context);
    }

    private Value runRequest(Value function, Value request) {
        return function.execute(request);
    }

    private Value runContextAndRequest(Value function, Value context, Value request) {
        return function.execute(context, request);
    }

    /**
     * 递归转换 Java 对象到 GraalVM JS 类型
     */
    private Value convertJavaToJS(Context context, Object javaObj) {
        if (javaObj == null) return null;

        if (javaObj instanceof Map) {
            // 处理 Map → JS 对象
            Value jsMap = context.eval("js", "({})");
            ((Map<?, ?>) javaObj).forEach((k, v) ->
                    jsMap.putMember(k.toString(), convertJavaToJS(context, v))
            );
            return jsMap;
        } else if (javaObj instanceof Iterable) {
            // 处理集合 → JS 数组
            Value jsArray = context.eval("js", "[]");
            int index = 0;
            for (Object item : (Iterable<?>) javaObj) {
                jsArray.setArrayElement(index++, convertJavaToJS(context, item));
            }
            return jsArray;
        } else if (javaObj.getClass().isArray()) {
            // 处理原生数组（如 String[]）
            return convertJavaToJS(context, Arrays.asList((Object[]) javaObj));
        } else {
            // 基本类型直接传递
            return context.asValue(javaObj);
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