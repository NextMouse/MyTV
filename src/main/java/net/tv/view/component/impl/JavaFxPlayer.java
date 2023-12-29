package net.tv.view.component.impl;

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
import net.tv.view.component.IMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JavaFxPlayer implements IMediaPlayer {

    private final MediaView mediaView;
    private String src;
    private Status status;

    private interface R {
        Background DEFAULT_BACKGROUND = new Background(
                new BackgroundFill(Color.BLACK, null, null));
    }

    private final JFXPanel jfxPanel;

    public JavaFxPlayer() {
        ConsoleLog.println("当前播放器：JavaFx Player");
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

        this.status = Status.INIT;
    }

    @Override
    public JComponent getJComponentPanel() {
        return this.jfxPanel;
    }

    @Override
    public void play(String src) {
        this.status = Status.LOADING;
        this.src = src;
        this.createMediaPlayer(src);
        this.mediaView.getMediaPlayer().setAutoPlay(true);
        this.status = Status.PLAYING;
    }

    @Override
    public void refresh() {
        this.release();
        this.play(this.src);
    }

    @Override
    public void stop() {
        this.mediaView.getMediaPlayer().stop();
        this.status = Status.STOPPED;
    }

    @Override
    public void pause(boolean pause) {
        if (pause) {
            this.mediaView.getMediaPlayer().pause();
            this.status = Status.PAUSED;
        } else {
            this.status = Status.LOADING;
            this.mediaView.getMediaPlayer().play();
            this.status = Status.PLAYING;
        }
    }

    @Override
    public void volume(double volume) {
        this.mediaView.getMediaPlayer().setVolume(volume / 100);
    }

    @Override
    public void mute(boolean mute) {
        this.mediaView.getMediaPlayer().setMute(mute);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void release() {
        this.status = Status.DESTROYING;
        this.mediaView.getMediaPlayer().stop();
        this.mediaView.getMediaPlayer().dispose();
        this.status = Status.DESTROYED;
    }

    public static class PlayerErrorHandler implements EventHandler<MediaErrorEvent> {
        @Override
        public void handle(MediaErrorEvent mediaErrorEvent) {
            ConsoleLog.println("播放错误：{}", mediaErrorEvent.getMediaError().getMessage());
        }
    }

    private void createMediaPlayer(String src) {
        if (this.mediaView.getMediaPlayer() != null) {
            release();
        }
        final Media media = new Media(src);
        ConsoleLog.println("视频宽高比：[{}×{}]", media.getWidth(), media.getHeight());
        this.mediaView.setMediaPlayer(new MediaPlayer(media));
    }

}
