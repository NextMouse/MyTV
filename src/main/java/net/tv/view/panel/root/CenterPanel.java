package net.tv.view.panel.root;

import net.tv.view.arm.GodHand;
import net.tv.view.panel.root.center.VideoManagerToolBar;
import net.tv.view.panel.root.center.VideoMenuToolBar;
import net.tv.view.panel.root.center.VideoPanel;

import javax.swing.*;
import java.awt.*;

public class CenterPanel extends JPanel {

    public CenterPanel() {
        setLayout(new BorderLayout());
        add(new VideoMenuToolBar(), BorderLayout.NORTH);
        add(new VideoPanel(), BorderLayout.CENTER);
        VideoManagerToolBar videoManagerToolBar = new VideoManagerToolBar();
        add(videoManagerToolBar, BorderLayout.SOUTH);
        GodHand.register(GodHand.K.VideoManagerToolBar, videoManagerToolBar);
    }

}
