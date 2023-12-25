package net.tv.view.panel.root.top;

import net.tv.view.config.SystemConfig;
import net.tv.service.orm.SqliteHelper;
import net.tv.view.WindowMain;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.Icons;
import net.tv.view.component.SimpleButton;
import net.tv.view.panel.popup.ConfigPopup;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SystemMenuPanel extends JToolBar {

    private final List<SimpleButton> buttonList = new ArrayList<>();

    {
        buttonList.add(new SimpleButton(Icons.Standard.CONFIG, "系统配置", (e, btn) -> {
            try {
                new ConfigPopup().open();
            } catch (Exception ex) {
                ConsoleLog.println(ex);
            }
        }));

        buttonList.add(new SimpleButton(Icons.Standard.SIZE_MIN, "最小化", (e, btn) -> {
            GodHand.<WindowMain>exec(GodHand.K.WindowMain, windowMain -> {
                windowMain.setExtendedState(JFrame.ICONIFIED);
            });
        }));

        buttonList.add(new SimpleButton(Icons.Standard.SIZE_NOT_MAX, "最大化", (e, btn) -> {
            WindowMain windowMain = GodHand.get(GodHand.K.WindowMain);
            int state = windowMain.getExtendedState();
            if ((state & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
                btn.setIcon(Icons.Standard.SIZE_MAX);
                windowMain.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                btn.setIcon(Icons.Standard.SIZE_NOT_MAX);
                windowMain.setExtendedState(JFrame.NORMAL);
            }
        }));

        buttonList.add(new SimpleButton(Icons.Standard.CLOSE, "关闭", (e, btn) -> {
            try {
                SqliteHelper.get().close();
                GodHand.exec(GodHand.K.SystemConfig, SystemConfig::save);
            } catch (Exception ex) {
                // ignore
            }
            System.exit(0);
        }));

    }

    public SystemMenuPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setFloatable(false);
        for (JButton jButton : buttonList) {
            add(jButton);
        }
    }

}
