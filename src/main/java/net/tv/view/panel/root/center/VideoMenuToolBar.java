package net.tv.view.panel.root.center;

import cn.hutool.core.util.StrUtil;
import net.tv.view.arm.GodHand;
import net.tv.view.component.Icons;
import net.tv.view.component.SimpleButton;
import net.tv.view.component.impl.MediaPlayerProxy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static javax.swing.SwingConstants.LEFT;
import static net.tv.view.arm.GodHand.K;

public class VideoMenuToolBar extends JPanel {

    interface R {
        Insets BUTTON_MARGIN = new Insets(0, 5, 0, 10);
        Font TEXT_FIELD_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    }

    public VideoMenuToolBar() {
        setLayout(new BorderLayout());
        add(new JLabel("链接：", Icons.Standard.VIDEO, LEFT), BorderLayout.WEST);
        JTextField mediaLinkTextFiled = new JTextField();
        mediaLinkTextFiled.setFont(R.TEXT_FIELD_FONT);
        add(mediaLinkTextFiled, BorderLayout.CENTER);
        GodHand.register(GodHand.K.MediaLinkTextFiled, mediaLinkTextFiled);
        SimpleButton loadButton = getLoadButton();
        add(loadButton, BorderLayout.EAST);

        mediaLinkTextFiled.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    loadButton.doClick();
                }
            }
        });

        setVisible(true);
    }

    private static SimpleButton getLoadButton() {
        SimpleButton loadButton = new SimpleButton("加 载", Icons.MIN.LOAD, (e, btn) -> {
            GodHand.<JTextField>exec(K.MediaLinkTextFiled, textField -> {
                final String mediaSrc = textField.getText();
                if (StrUtil.isNotBlank(mediaSrc)) {
                    GodHand.<MediaPlayerProxy>exec(K.MediaPlayerProxy, player -> player.play(mediaSrc));
                }
            });
        });
        loadButton.setMargin(R.BUTTON_MARGIN);
        loadButton.setBackground(null);
        return loadButton;
    }

}
