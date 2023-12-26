package net.tv.view.component.impl;

import cn.hutool.core.util.StrUtil;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.component.IMediaPlayer;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;

public class VlcPlayer implements IMediaPlayer {

    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private String src;
    private Status status;

    public VlcPlayer() {
        boolean discover = new NativeDiscovery().discover();
        ConsoleLog.println("VLC Player {}", (discover ? "已找到" : "未找到"));

        this.mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        this.mediaPlayerComponent.setBackground(Color.BLACK);

        this.mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventListener());
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
        this.mediaPlayerComponent.mediaPlayer().media().start(src);
        this.status = Status.PLAYING;
    }

    @Override
    public void refresh() {
        this.stop();
        this.play(this.src);
    }

    @Override
    public void stop() {
        mediaPlayerComponent.mediaPlayer().controls().stop();
        this.status = Status.STOPPED;
    }

    @Override
    public void pause(boolean pause) {
        mediaPlayerComponent.mediaPlayer().controls().pause();
        this.status = Status.PAUSED;
    }

    @Override
    public void volume(double volume) {
        mediaPlayerComponent.mediaPlayer().audio().setVolume((int) volume);
    }

    @Override
    public void mute(boolean mute) {
        mediaPlayerComponent.mediaPlayer().audio().setMute(mute);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    static class MediaPlayerEventListener extends MediaPlayerEventAdapter {
        @Override
        public void opening(MediaPlayer mediaPlayer) {
            ConsoleLog.println("正在打开：{}", mediaPlayer.media().info().mrl());
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            ConsoleLog.println("打开成功，视频宽高比[{}]，视频类型：{}，标题：{}",
                    mediaPlayer.video().aspectRatio(),
                    mediaPlayer.media().info().type(),
                    mediaPlayer.video().trackDescriptions()
            );
        }

        @Override
        public void error(MediaPlayer mediaPlayer) {
            ConsoleLog.println("播放错误");
        }
    }

}
