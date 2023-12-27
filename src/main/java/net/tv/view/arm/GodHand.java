package net.tv.view.arm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class GodHand {

    public enum K {
        WindowMain,
        MediaLinkTextFiled,
        IMediaPlayer,
        GroupListPanel,
        GroupItemListPanel,
        VideoManagerToolBar,
        SystemConfig,
        PlaylistService,
    }

    /**
     * 组件引用管理器
     */
    private static final Map<K, Object> objMap = new HashMap<>();

    private GodHand() {

    }

    public static void register(K key, Object obj) {
        assert obj != null;
        objMap.put(key, obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(K key) {
        return (T) objMap.get(key);
    }

    public static <T> void exec(K key, Consumer<T> consumer) {
        try {
            consumer.accept(get(key));
        } catch (Exception e) {
            ConsoleLog.println("GodHand Error：{}", e.getMessage());
        }
    }

}
