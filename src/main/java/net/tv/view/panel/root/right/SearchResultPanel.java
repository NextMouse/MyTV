package net.tv.view.panel.root.right;

import lombok.Getter;
import net.tv.service.model.PlayViewItem;

import javax.swing.*;
import java.awt.*;

@Getter
public class SearchResultPanel extends JPanel {

    interface R {
        Font FONT_CHANNEL_TITLE = new Font(Font.MONOSPACED, Font.BOLD, 13);
        Font FONT_MEDIA_URL = new Font(Font.SERIF, Font.ITALIC, 11);
    }

    private final JLabel channelTitleLabel;
    private final JLabel mediaUrlLabel;
    private final JLabel numLabel;
    private final JPanel resultPanel;
    private final PlayViewItem playViewItem;

    public SearchResultPanel(int index, PlayViewItem playViewItem) {
        this.playViewItem = playViewItem;

        channelTitleLabel = new JLabel(" " + playViewItem.getChannelTitle());
        channelTitleLabel.setFont(R.FONT_CHANNEL_TITLE);

        mediaUrlLabel = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;" + playViewItem.getMediaUrl() + "</html>");
        mediaUrlLabel.setFont(R.FONT_MEDIA_URL);

        numLabel = new JLabel(String.format("<html>&nbsp;%02d</html>", index));

        setLayout(new BorderLayout());

        resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(channelTitleLabel, BorderLayout.NORTH);
        resultPanel.add(mediaUrlLabel, BorderLayout.CENTER);

        add(numLabel, BorderLayout.WEST);
        add(resultPanel, BorderLayout.CENTER);

    }

}
