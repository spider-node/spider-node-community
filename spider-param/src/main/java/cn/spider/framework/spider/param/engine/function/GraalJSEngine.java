package cn.spider.framework.spider.param.engine.function;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.Map;

public class GraalJSEngine {
    private final Context context;

    private final String[] PERMITTED_LANGUAGES = {"js"};

    private Map<String, Value> functionMap;

    public GraalJSEngine() {
        this.context = Context.newBuilder(PERMITTED_LANGUAGES)
                .allowAllAccess(true)
                // .allowCreateThread(true)
                .build();
        this.functionMap = new HashMap<>();
    }

    public void load(String js,String functionName) {
        Value jsFunction = context.eval("js", js).getMember(functionName);
        this.functionMap.put(functionName, jsFunction);
    }

}
