package net.tv.view.component;

import javax.swing.*;
import java.awt.*;

public interface CustomizeComponent<T extends JComponent> {

    interface R {
        Color DEFAULT_FOREGROUND = Color.decode("#C0C6C9");
        Color MOVED_FOREGROUND = Color.decode("#424C50");
        Color MOVED_BACKGROUND = Color.decode("#C0EBD7");
    }

    default void onInitialize(JList<T> jList, T comp) {
        jList.setSelectionBackground(null);
        jList.setSelectionForeground(null);
        comp.setBackground(jList.getBackground());
    }

    default void onDefault(JList<T> jList, T comp) {
        comp.setForeground(R.DEFAULT_FOREGROUND);
        comp.setBackground(null);
    }

    default void onSelected(JList<T> jList, T comp) {
        comp.setForeground(Color.WHITE);
        comp.setBackground(Color.PINK);
    }

    default void onMouseMoved(JList<T> jList, T comp) {
        comp.setBackground(R.MOVED_BACKGROUND);
        comp.setForeground(R.MOVED_FOREGROUND);
    }

}
