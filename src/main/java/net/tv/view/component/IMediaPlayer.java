package net.tv.view.component;

import javax.swing.*;

public interface IMediaPlayer {

    enum Status {
        INIT,    // 初始化
        PLAYING, // 播放中
        STOPPED, // 停止中
        PAUSED,  // 暂停中
    }

    JComponent getJComponentPanel();

    void play(String src);

    void refresh();

    void stop();

    void pause(boolean pause);

    void volume(double volume);

    void mute(boolean mute);

    Status getStatus();

    void release();
}
