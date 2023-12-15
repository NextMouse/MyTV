package net.tv.view.component;

import cn.hutool.core.util.StrUtil;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaErrorEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.panel.root.center.VideoManagerToolBar;

import javax.swing.*;

public class MediaPlayerManager {

    private String uri;

    private MediaView mediaView;

    private MediaStateInfo mediaStateInfo;

    public void refresh() {
        load(mediaStateInfo.mediaUri);
    }

    public void setSilent(boolean silent) {
        final MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
        if (mediaPlayer == null) {
            return;
        }
        if (silent) {
            mediaPlayer.setVolume(0);
        } else {
            mediaPlayer.setVolume(mediaStateInfo.lastVolume);
        }
    }

    public void setVolume(int value) {
        final MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(value);
            mediaStateInfo.lastVolume = value;
        }
    }

    public class MediaStateInfo {

        /**
         * 当前播放的媒体
         */
        public String mediaUri;

        /**
         * 最后的音量
         */
        public double lastVolume;

        /**
         * 播放状态
         */
        public boolean playStatus;
    }

    private MediaPlayerManager() {
        mediaView = new MediaView();
        mediaView.setOnError(new PlayerErrorHandler());
        mediaView.setSmooth(true);
        mediaStateInfo = new MediaStateInfo();
    }

    public static MediaPlayerManager instance() {
        return new MediaPlayerManager();
    }

    public MediaView getMediaView() {
        return this.mediaView;
    }

    public MediaPlayerManager load(String uri) {
        if (StrUtil.isBlank(uri)) {
            ConsoleLog.println("请输入媒体地址");
            return this;
        }
        this.uri = uri;
        final MediaPlayer oldMediaPlay = mediaView.getMediaPlayer();
        if (oldMediaPlay != null) {
            new Thread(() -> {
                oldMediaPlay.dispose();
            }).start();
        }
        mediaView.setMediaPlayer(new MediaPlayer(new Media(uri)));
        mediaStateInfo.lastVolume = mediaView.getMediaPlayer().getVolume();
        mediaStateInfo.mediaUri = uri;
        return this;
    }

    public void play() {
        if (StrUtil.isNotBlank(uri)) {
            ConsoleLog.println("打开媒体：{}", uri);
            GodHand.<JTextField>exec(GodHand.K.MediaLinkTextFiled, mediaText -> mediaText.setText(uri));
            GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, videoManagerToolBar -> videoManagerToolBar.setVolumeSliderEnabled(true));
            mediaStateInfo.playStatus = true;
            mediaView.getMediaPlayer().play();
        }
    }

    public void pause() {
        if (StrUtil.isNotBlank(uri)) {
            ConsoleLog.println("暂停媒体：{}", uri);
        }
        mediaView.getMediaPlayer().pause();
        mediaStateInfo.playStatus = false;
    }

    public void stop() {
        mediaView.getMediaPlayer().stop();
        mediaStateInfo.playStatus = false;
    }

    public double getLastVolume() {
        if (getMediaPlayer() == null) {
            return -1d;
        }
        return mediaStateInfo.lastVolume;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaView.getMediaPlayer();
    }

    public void resize(int width, int height) {
        mediaView.setFitWidth(width);
        mediaView.setFitHeight(height);
        mediaView.setPreserveRatio(true);
    }

    public MediaStateInfo stateInfo() {
        return mediaStateInfo;
    }

    public class PlayerErrorHandler implements EventHandler<MediaErrorEvent> {
        @Override
        public void handle(MediaErrorEvent mediaErrorEvent) {
            ConsoleLog.println(mediaErrorEvent.getMediaError());
        }
    }

}
