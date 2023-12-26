package net.tv.view.component.impl;

import cn.hutool.core.util.StrUtil;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaErrorEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.IMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JavaFxPlayer implements IMediaPlayer {

    private final MediaView mediaView;
    private String src;
    private double volume;
    private Status status;

    private interface R {
        Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.BLACK, null, null));
    }

    private final JFXPanel jfxPanel;

    public JavaFxPlayer() {
        this.jfxPanel = new JFXPanel();

        this.mediaView = new MediaView();
        this.mediaView.setOnError(new PlayerErrorHandler());
        this.mediaView.setSmooth(true);

        BorderPane mediaPlayPane = new BorderPane();
        mediaPlayPane.setBackground(R.DEFAULT_BACKGROUND);
        mediaPlayPane.setCenter(this.mediaView);

        this.jfxPanel.setScene(new Scene(mediaPlayPane));
        this.jfxPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Component comp = e.getComponent();
                mediaView.setFitWidth(comp.getWidth());
                mediaView.setFitHeight(comp.getHeight());
                mediaView.setPreserveRatio(true);
            }
        });

        GodHand.register(GodHand.K.IMediaPlayer, this);

        this.status = Status.INIT;
    }

    @Override
    public JComponent getJComponentPanel() {
        return this.jfxPanel;
    }

    @Override
    public void play(String src) {
        if (StrUtil.isBlank(src)) {
            ConsoleLog.println("请输入媒体地址");
            return;
        }
        this.src = src;
        this.createMediaPlayer(src);
        this.volume = this.mediaView.getMediaPlayer().getVolume();
        this.mediaView.getMediaPlayer().setAutoPlay(true);
        this.status = Status.PLAYING;
    }

    @Override
    public void refresh() {
        this.createMediaPlayer(this.src);
        this.mediaView.getMediaPlayer().setAutoPlay(true);
        this.status = Status.PLAYING;
    }

    @Override
    public void stop() {
        if (this.mediaView.getMediaPlayer() != null) {
            this.mediaView.getMediaPlayer().stop();
            this.status = Status.STOPPED;
        }
    }

    @Override
    public void pause(boolean pause) {
        if (this.mediaView.getMediaPlayer() != null) {
            if (pause) {
                this.mediaView.getMediaPlayer().pause();
                this.status = Status.PAUSED;
            } else {
                this.mediaView.getMediaPlayer().play();
                this.status = Status.PLAYING;
            }
        }
    }

    @Override
    public void volume(double volume) {
        if (this.mediaView.getMediaPlayer() != null) {
            this.mediaView.getMediaPlayer().setVolume(volume);
            this.volume = volume;
        }
    }

    @Override
    public void mute(boolean mute) {
        if (this.mediaView.getMediaPlayer() != null) {
            this.mediaView.getMediaPlayer().setMute(mute);
        }
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    public static class PlayerErrorHandler implements EventHandler<MediaErrorEvent> {
        @Override
        public void handle(MediaErrorEvent mediaErrorEvent) {
            ConsoleLog.println(mediaErrorEvent.getMediaError().getMessage());
        }
    }

    private void createMediaPlayer(String src) {
        // 销毁旧的
        if (this.mediaView.getMediaPlayer() != null) {
            this.mediaView.getMediaPlayer().stop();
            this.mediaView.getMediaPlayer().dispose();
        }
        this.mediaView.setMediaPlayer(new MediaPlayer(new Media(src)));
    }

}
