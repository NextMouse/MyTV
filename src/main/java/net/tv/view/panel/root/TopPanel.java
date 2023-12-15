package net.tv.view.panel.root;

import net.tv.view.panel.root.top.SystemMenuPanel;
import net.tv.view.panel.root.top.TopTitlePanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TopPanel extends JPanel {

    private interface R {
        Border BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 0);
    }

    public TopPanel() {
        // 设置边框
        setLayout(new BorderLayout());
        add(new TopTitlePanel(), BorderLayout.CENTER);
        add(new SystemMenuPanel(), BorderLayout.EAST);
        setBorder(R.BORDER);
    }


}
