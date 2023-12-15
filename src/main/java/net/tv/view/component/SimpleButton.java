package net.tv.view.component;

import net.tv.view.arm.ConsoleLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

public class SimpleButton extends JButton {

    private SimpleButton me = this;

    public SimpleButton(String name, BiConsumer<ActionEvent, JButton> consumer) {
        setText(name);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener((e) -> {
            try {
                consumer.accept(e, me);
            } catch (Exception ex) {
                ConsoleLog.println(ex.getMessage());
            }
        });

    }

    public SimpleButton(ImageIcon icon, String tip, BiConsumer<ActionEvent, JButton> consumer) {
        setIcon(icon);
        setToolTipText(tip);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener((e) -> {
            try {
                consumer.accept(e, me);
            } catch (Exception ex) {
                ConsoleLog.println(ex.getMessage());
            }
        });
    }

    public SimpleButton(String name, ImageIcon icon, BiConsumer<ActionEvent, JButton> consumer) {
        setText(name);
        setIcon(icon);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addActionListener((e) -> {
            try {
                consumer.accept(e, me);
            } catch (Exception ex) {
                ex.printStackTrace();
                ConsoleLog.println(ex.getMessage());
            }
        });
    }

}
