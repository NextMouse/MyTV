package net.tv.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncUtil {

    private AsyncUtil() {
    }

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void exec(Runnable runnable) {
        executor.execute(runnable);
    }

}
