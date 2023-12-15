package net.tv.view.component;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.tv.view.arm.GodHand;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MediaPanel extends JFXPanel {

    private MediaPlayerManager mediaPlayerManager;

    private interface R {
        Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.BLACK, null, null));
    }

    public MediaPanel() {
        BorderPane mediaPlayPane = new BorderPane();
        mediaPlayerManager = MediaPlayerManager.instance();
        mediaPlayPane.setCenter(mediaPlayerManager.getMediaView());
        setScene(new Scene(mediaPlayPane));
        mediaPlayPane.setBackground(R.DEFAULT_BACKGROUND);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Component comp = e.getComponent();
                GodHand.<MediaPlayerManager>exec(GodHand.K.MediaPlayerManager, playerManager -> {
                    playerManager.resize(comp.getWidth(), comp.getHeight());
                });
            }
        });
        GodHand.register(GodHand.K.MediaPlayerManager, mediaPlayerManager);
    }

    /**
     * 设置场景大小
     */
    public void resize() {
        mediaPlayerManager.resize(getWidth(), getHeight());
    }

}
