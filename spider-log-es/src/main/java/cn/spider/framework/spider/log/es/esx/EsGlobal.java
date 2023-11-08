package cn.spider.framework.spider.log.es.esx;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.0
 */
public class EsGlobal {
    private static Consumer<EsCommandHolder> onCommandBefore;
    private static Consumer<EsCommandHolder> onCommandAfter;

    public static void onCommandBefore(Consumer<EsCommandHolder> event) {
        onCommandBefore = event;
    }

    public static void onCommandAfter(Consumer<EsCommandHolder> event) {
        onCommandAfter = event;
    }


    public static void applyCommandBefore(EsCommandHolder cmd) {
        if (onCommandBefore != null) {
            onCommandBefore.accept(cmd);
        }
    }

    public static void applyCommandAfter(EsCommandHolder cmd) {
        if (onCommandAfter != null) {
            onCommandAfter.accept(cmd);
        }
    }
}
