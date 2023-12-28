package net.tv.view.component.impl;

import cn.hutool.core.util.StrUtil;
import net.tv.util.AsyncUtil;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.IMediaPlayer;
import net.tv.view.panel.root.center.VideoManagerToolBar;

import javax.swing.*;

public class MediaPlayerProxy {

    private final IMediaPlayer mediaPlayer;

    public JComponent getJComponentPanel() {
        return mediaPlayer.getJComponentPanel();
    }

    public MediaPlayerProxy(IMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void play(String src) {
        if (StrUtil.isBlank(src)) {
            ConsoleLog.println("播放地址不能为空");
            return;
        }
        ConsoleLog.println("准备播放：{}", src.trim());
        AsyncUtil.exec(() -> mediaPlayer.play(src.trim()));
        GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, toolBar -> toolBar.getVolumeSlider().setEnabled(true));
    }

    public void refresh() {
        mediaPlayer.refresh();
    }

    public void stop() {
        if (mediaPlayer.getStatus() == IMediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
        }
    }

    public void pause(boolean pause) {
        if (mediaPlayer.getStatus() == IMediaPlayer.Status.PLAYING) {
            mediaPlayer.pause(pause);
        }
    }

    public void volume(double volume) {
        mediaPlayer.volume(volume);
    }

    public void mute(boolean mute) {
        mediaPlayer.mute(mute);
    }

    public IMediaPlayer.Status getStatus() {
        return mediaPlayer.getStatus();
    }

    public void release() {
        mediaPlayer.release();
    }


}
