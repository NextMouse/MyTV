package net.tv.view.panel.root.right;

import net.tv.service.model.PlayViewItem;

import javax.swing.*;
import java.awt.*;

public class SearchResultPanel extends JPanel {

    interface R {
        Font FONT_CHANNEL_TITLE = new Font(Font.MONOSPACED, Font.BOLD, 13);
        Font FONT_MEDIA_URL = new Font(Font.SERIF, Font.ITALIC, 11);
    }

    private JLabel channelTitleLabel;
    private JLabel mediaUrlLabel;

    private PlayViewItem playViewItem;

    public SearchResultPanel(PlayViewItem playViewItem) {
        this.playViewItem = playViewItem;

        channelTitleLabel = new JLabel(" " + playViewItem.getChannelTitle());
        channelTitleLabel.setFont(R.FONT_CHANNEL_TITLE);

        mediaUrlLabel = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;" + playViewItem.getMediaUrl() + "</html>");
        mediaUrlLabel.setFont(R.FONT_MEDIA_URL);

        setLayout(new BorderLayout());
        add(channelTitleLabel, BorderLayout.NORTH);
        add(mediaUrlLabel, BorderLayout.CENTER);

    }

    public JLabel getMediaUrlLabel() {
        return this.mediaUrlLabel;
    }

    public JLabel getChannelTitleLabel() {
        return this.channelTitleLabel;
    }

    public PlayViewItem getPlayViewItem() {
        return this.playViewItem;
    }

}
