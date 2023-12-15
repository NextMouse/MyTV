package net.tv;

import net.tv.view.WindowMain;
import net.tv.view.arm.GodHand;
import net.tv.view.component.MediaPlayerManager;

public class Main {

    public static void main(String[] args) {

        WindowMain.run();

//        GodHand.<MediaPlayerManager>exec(GodHand.K.MediaPlayerManager, playerManager -> {
//            playerManager.load("https://canal.mediaserver.com.co/live/MelodyChannel.m3u8");
//            playerManager.play();
//        });

    }

}