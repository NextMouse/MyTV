package net.tv.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class AsyncUtil {

    private AsyncUtil() {
    }

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void exec(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void exec(Runnable runnable, int timeout, TimeUnit unit) {
        Future<?> future = executor.submit(runnable);
        try {
            future.get(timeout, unit);
        } catch (Exception ex) {
            future.cancel(true);
        }
    }

}
