package net.tv.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScrollListAndTitlePanel<T extends JComponent> extends JPanel {

    interface R {
        int HEIGHT = 20;
    }

    public JLabel titleLabel;


    public CustomizeList<T> customizeList;

    public ScrollListAndTitlePanel(String title, int width, Consumer<T> consumer) {

        // 初始化组件
        titleLabel = new JLabel(" " + title);
        titleLabel.setPreferredSize(new Dimension(getWidth(), R.HEIGHT));
        titleLabel.setVerticalAlignment(Label.CENTER);

        // 设置布局
        setLayout(new BorderLayout());
        setBackground(null);

        add(titleLabel, BorderLayout.NORTH);
        customizeList = new CustomizeList<>(getCustomizeComponent(), consumer);
        customizeList.setMouseRightClickEvent(getMouseConsumer());
        JScrollPane scrollPane = new JScrollPane(customizeList);
        scrollPane.setForeground(null);
        scrollPane.setBackground(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(width, titleLabel.getHeight() + scrollPane.getHeight()));
    }


    public CustomizeComponent getCustomizeComponent() {
        return new CustomizeComponent() {
        };
    }

    public BiConsumer<MouseEvent, T> getMouseConsumer() {
        return null;
    }

}
