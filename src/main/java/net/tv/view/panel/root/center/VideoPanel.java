package net.tv.view.panel.root.center;

import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.impl.JavaFxPlayer;
import net.tv.view.component.impl.MediaPlayerProxy;
import net.tv.view.component.impl.VlcPlayer;

import javax.swing.*;
import java.awt.*;

public class VideoPanel extends JPanel {

    private interface R {
        Color BACKGROUND_COLOR = Color.BLACK;
    }

    public VideoPanel() {
        setLayout(new BorderLayout());
        setBackground(R.BACKGROUND_COLOR);
        MediaPlayerProxy mediaPlayerProxy;
        try {
            mediaPlayerProxy = new MediaPlayerProxy(new VlcPlayer());
        } catch (Exception ex) {
            ConsoleLog.println("当前使用 JavaFX MediaPlayer");
            mediaPlayerProxy = new MediaPlayerProxy(new JavaFxPlayer());
        }
        add(mediaPlayerProxy.getJComponentPanel(), BorderLayout.CENTER);
        GodHand.register(GodHand.K.MediaPlayerProxy, mediaPlayerProxy);
    }

}
