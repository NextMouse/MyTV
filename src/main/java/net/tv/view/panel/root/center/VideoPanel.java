package net.tv.view.panel.root.center;

import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.IMediaPlayer;
import net.tv.view.component.impl.JavaFxPlayer;
import net.tv.view.component.impl.MediaPlayerProxy;
import net.tv.view.component.impl.VlcPlayer;
import net.tv.view.config.SystemConfig;

import javax.swing.*;
import java.awt.*;

public class VideoPanel extends JPanel {

    private interface R {
        Color BACKGROUND_COLOR = Color.BLACK;
    }

    private JComponent videoComponent;

    public VideoPanel() {
        setLayout(new BorderLayout());
        setBackground(R.BACKGROUND_COLOR);
        MediaPlayerProxy mediaPlayerProxy;
        SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
        if (MediaPlayerProxy.MediaPlayerType.VLC == systemConfig.getMediaPlayerType()) {
            try {
                mediaPlayerProxy = new MediaPlayerProxy(new VlcPlayer());
                systemConfig.setMediaPlayerType(MediaPlayerProxy.MediaPlayerType.VLC);
            } catch (Exception ex) {
                ConsoleLog.println("未找到VLC Player, 当前使用 JavaFX MediaPlayer");
                mediaPlayerProxy = new MediaPlayerProxy(new JavaFxPlayer());
                systemConfig.setMediaPlayerType(MediaPlayerProxy.MediaPlayerType.JavaFx);
            }
        } else {
            mediaPlayerProxy = new MediaPlayerProxy(new JavaFxPlayer());
            systemConfig.setMediaPlayerType(MediaPlayerProxy.MediaPlayerType.JavaFx);
        }
        videoComponent = mediaPlayerProxy.getJComponentPanel();
        add(videoComponent, BorderLayout.CENTER);
        GodHand.register(GodHand.K.MediaPlayerProxy, mediaPlayerProxy);
        GodHand.register(GodHand.K.VideoPanel, this);
    }

    public void changeMediaPlayer(IMediaPlayer mediaPlayer) {
        if (videoComponent != null) {
            this.remove(videoComponent);
        }
        videoComponent = mediaPlayer.getJComponentPanel();
        add(videoComponent, BorderLayout.CENTER);
        GodHand.register(GodHand.K.MediaPlayerProxy, new MediaPlayerProxy(mediaPlayer));
    }

}
