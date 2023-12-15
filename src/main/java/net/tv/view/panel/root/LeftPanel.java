package net.tv.view.panel.root;

import net.tv.view.panel.root.left.FileMenuToolBar;
import net.tv.view.panel.root.left.GroupItemListPanel;
import net.tv.view.panel.root.left.GroupListPanel;

import javax.swing.*;
import java.awt.*;

public class LeftPanel extends JPanel {

    private interface R {
    }

    public LeftPanel() {
        // 布局
        setLayout(new BorderLayout());
        // 添加组件
        add(new FileMenuToolBar(), BorderLayout.NORTH);
        add(new GroupListPanel(), BorderLayout.WEST);
        add(new GroupItemListPanel(), BorderLayout.CENTER);
    }
}
