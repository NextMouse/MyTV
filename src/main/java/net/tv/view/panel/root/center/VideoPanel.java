package net.tv.view.panel.root.center;

import net.tv.view.arm.GodHand;
import net.tv.view.component.MediaPanel;

import javax.swing.*;
import java.awt.*;

public class VideoPanel extends JPanel {

    private interface R {
        Color BACKGROUND_COLOR = Color.BLACK;
    }

    public VideoPanel() {
        setLayout(new BorderLayout());
        // 视频播放区域
        MediaPanel mediaPanel = new MediaPanel();
        add(mediaPanel, BorderLayout.CENTER);
        setBackground(R.BACKGROUND_COLOR);
        GodHand.register(GodHand.K.MediaPanel, mediaPanel);
    }

}
