package net.tv.view.component.impl;

import cn.hutool.core.util.StrUtil;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.component.IMediaPlayer;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
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
        MediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.controls().stop();
            this.status = Status.STOPPED;
        }
    }

    @Override
    public void pause(boolean pause) {
        MediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.controls().pause();
            this.status = Status.PAUSED;
        }
    }

    @Override
    public void volume(double volume) {
        MediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.audio().setVolume((int) volume);
        }
    }

    @Override
    public void mute(boolean mute) {
        MediaPlayer mediaPlayer = mediaPlayerComponent.mediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.audio().setMute(mute);
        }
    }

    @Override
    public Status getStatus() {
        return this.status;
    }


}
