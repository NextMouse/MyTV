package net.tv.view.panel.root.center;

import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.IMediaPlayer;
import net.tv.view.component.impl.JavaFxPlayer;
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

        IMediaPlayer mediaPlayer;
        try {
            mediaPlayer = new VlcPlayer();
        } catch (Exception ex) {
            ConsoleLog.println("当前使用 JavaFX MediaPlayer");
            mediaPlayer = new JavaFxPlayer();
        }
        add(mediaPlayer.getJComponentPanel(), BorderLayout.CENTER);
        GodHand.register(GodHand.K.IMediaPlayer, mediaPlayer);

    }

}
