package net.tv.view.component;

import javax.swing.*;
import java.awt.*;

public interface CustomizeComponent<T extends JComponent> {

    interface R {
        Color DEFAULT_FOREGROUND = Color.decode("#4B5CC4");
        Color MOVED_FOREGROUND = Color.decode("#4B5CC4");
        Color MOVED_BACKGROUND = Color.decode("#F3F9F1");
        Color SELECTED_FOREGROUND = Color.decode("#003472");
        Color SELECTED_BACKGROUND = Color.decode("#2ADD9C");
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
        comp.setForeground(R.SELECTED_FOREGROUND);
        comp.setBackground(R.SELECTED_BACKGROUND);
    }

    default void onMouseMoved(JList<T> jList, T comp) {
        comp.setBackground(R.MOVED_BACKGROUND);
        comp.setForeground(R.MOVED_FOREGROUND);
    }

}
