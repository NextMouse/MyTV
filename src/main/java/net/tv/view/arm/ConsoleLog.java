package net.tv.view.arm;

import cn.hutool.core.util.StrUtil;
import net.tv.view.panel.root.center.VideoManagerToolBar;

public class ConsoleLog {

    public static void println(String str) {
        printlnToConsole(str);
    }

    public static void println(String format, Object... values) {
        printlnToConsole(StrUtil.format(format, values));
    }

    private static void printlnToConsole(String str) {
        System.out.println(str);
        GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, toolBar -> {
            if (toolBar != null) {
                toolBar.appendConsoleLog(str + System.lineSeparator());
            }
        });
    }

    public static void println(Throwable ex) {
        printlnToConsole(ex.toString());
    }
}
