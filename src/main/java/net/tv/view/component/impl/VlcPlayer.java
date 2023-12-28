package net.tv.view.component.impl;

import net.tv.view.arm.ConsoleLog;
import net.tv.view.component.IMediaPlayer;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;

public class VlcPlayer implements IMediaPlayer {
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private EmbeddedMediaPlayer mediaPlayer;
    private String src;
    private Status status;

    public VlcPlayer() {
        boolean discover = new NativeDiscovery().discover();
        ConsoleLog.println("VLC Player {}", (discover ? "已找到" : "未找到"));
        this.mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        this.mediaPlayer = this.mediaPlayerComponent.mediaPlayer();
    }

    private void createEmbeddedMediaPlayer() {
        this.mediaPlayer = mediaPlayerComponent.mediaPlayerFactory().mediaPlayers().newEmbeddedMediaPlayer();
        this.mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventListener());
        this.mediaPlayer.videoSurface().set(mediaPlayerComponent.mediaPlayerFactory().videoSurfaces().newVideoSurface(mediaPlayerComponent.videoSurfaceComponent()));
        System.gc();
    }

    @Override
    public JComponent getJComponentPanel() {
        return this.mediaPlayerComponent;
    }

    @Override
    public void play(String src) {
        this.src = src;
//        if (Status.INIT != getStatus()) {
//            createEmbeddedMediaPlayer();
//        }
        if (this.status != Status.PLAYING) {
            stop();
        }
        this.status = Status.LOADING;
        try {
            this.mediaPlayer.media().play(src);
        } catch (Exception ex) {
            // ignore
            ex.printStackTrace();
        }
        this.status = Status.PLAYING;
    }

    @Override
    public void refresh() {
        if (this.mediaPlayer != null) {
            this.stop();
            this.play(this.src);
        }
    }

    @Override
    public void stop() {
        this.mediaPlayer.controls().stop();
        this.status = Status.STOPPED;
    }

    @Override
    public void pause(boolean pause) {
        this.mediaPlayer.controls().setPause(pause);
        this.status = Status.PAUSED;
    }

    @Override
    public void volume(double volume) {
        this.mediaPlayer.audio().setVolume((int) volume);
    }

    @Override
    public void mute(boolean mute) {
        this.mediaPlayer.audio().setMute(mute);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void release() {
        this.src = null;
        this.status = Status.DESTROYING;
        this.mediaPlayer.release();
        this.status = Status.DESTROYED;
    }

    static class MediaPlayerEventListener extends MediaPlayerEventAdapter {
        @Override
        public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            StringBuilder strBuilder = new StringBuilder("正在播放：");
            final Dimension viedoDimension = mediaPlayer.video().videoDimension();
            if (viedoDimension != null) {
                strBuilder.append("[").append(viedoDimension.getWidth()).append("×").append(viedoDimension.getHeight()).append("]");
            }
            strBuilder.append(", ").append(mediaPlayer.media().info().mrl());
            ConsoleLog.println(strBuilder.toString());
        }
    }

}
