package net.tv.view.component.impl;

import cn.hutool.core.util.StrUtil;
import net.tv.util.AsyncUtil;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.component.IMediaPlayer;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class VlcPlayer implements IMediaPlayer {

    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private String src;
    private Status status;

    public VlcPlayer() {
        boolean discover = new NativeDiscovery().discover();
        ConsoleLog.println("VLC Player {}", (discover ? "已找到" : "未找到"));
        createEmbeddedMediaPlayerComponent();
    }

    private void createEmbeddedMediaPlayerComponent() {
        this.mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        this.mediaPlayerComponent.setBackground(Color.BLACK);
        this.mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventListener());
    }

    public MediaPlayer getMediaPlayer() {
        return this.mediaPlayerComponent.mediaPlayer();
    }

    @Override
    public JComponent getJComponentPanel() {
        return this.mediaPlayerComponent;
    }

    @Override
    public void play(String src) {
        if (StrUtil.isBlank(src)) {
            ConsoleLog.println("请输入媒体地址");
            return;
        }
        this.src = src;
        this.status = Status.LOADING;
        AsyncUtil.exec(() -> {
            ConsoleLog.println("当前播放：{}", src);
            getMediaPlayer().media().play(src);
            this.status = Status.PLAYING;

        }, 5, TimeUnit.SECONDS);

    }

    @Override
    public void refresh() {
        this.stop();
        this.play(this.src);
    }

    @Override
    public void stop() {
        if (getMediaPlayer().status().isPlaying()) {
            getMediaPlayer().controls().stop();
            this.status = Status.STOPPED;
        }
    }

    @Override
    public void pause(boolean pause) {
        if (getMediaPlayer().status().isPlaying()) {
            getMediaPlayer().controls().pause();
            this.status = Status.PAUSED;
        }
    }

    @Override
    public void volume(double volume) {
        getMediaPlayer().audio().setVolume((int) volume);
    }

    @Override
    public void mute(boolean mute) {
        getMediaPlayer().audio().setMute(mute);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void release() {
        if (getMediaPlayer() != null) {
            getMediaPlayer().release();
        }
    }

    static class MediaPlayerEventListener extends MediaPlayerEventAdapter {
        @Override
        public void playing(MediaPlayer mediaPlayer) {
            Dimension videoDimension = mediaPlayer.video().videoDimension();
            ConsoleLog.println("打开成功，宽高比：[{}]，类型：{}",
                    videoDimension == null ? "未知" : videoDimension.getWidth() + "×" + videoDimension.getHeight(),
                    mediaPlayer.media().info().type()
            );
        }

    }

}
