package net.tv.view.panel.root;

import net.tv.view.panel.root.right.RightPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class RootPanel extends JPanel {

    private interface R {
        Border BORDER = BorderFactory.createLineBorder(null, 1, true);
    }

    private RootPanel() {
        // 设置布局
        setLayout(new BorderLayout());
        // 添加上面板
        add(new TopPanel(), BorderLayout.NORTH);
        // 添加左面板
        add(new LeftPanel(), BorderLayout.WEST);
        // 添加中面板
        add(new CenterPanel(), BorderLayout.CENTER);
        // 添加有面板
        add(new RightPanel(), BorderLayout.EAST);
        // 设置边框
        setBorder(R.BORDER);
    }

    public static RootPanel instance() {
        RootPanel rootPanel = new RootPanel();
        rootPanel.setVisible(true);
        return rootPanel;
    }

}
