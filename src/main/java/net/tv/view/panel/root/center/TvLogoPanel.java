package net.tv.view.panel.root.center;

import cn.hutool.core.util.StrUtil;
import net.tv.util.ImageDownloadUtil;
import net.tv.view.arm.GodHand;
import net.tv.view.component.Icons;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TvLogoPanel extends JPanel {

    private final JLabel logo = new JLabel(Icons.DEFAULT_TV_LOGO);

    interface R {
        Dimension LOGO_LABEL_SIZE = new Dimension(200, 120);
        Color BACKGROUND = Color.decode("#2b2b2b");
        Border LOGO_PANEL_BORDER = new LineBorder(BACKGROUND, 1, false);
    }

    public TvLogoPanel() {
        setLayout(new BorderLayout());
        add(logo, BorderLayout.CENTER);
        setPreferredSize(R.LOGO_LABEL_SIZE);
        setMinimumSize(R.LOGO_LABEL_SIZE);
        setBorder(R.LOGO_PANEL_BORDER);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, toolBar -> {
                    if (StrUtil.isNotBlank(toolBar.getTvgLogValue())) {
                        setTvLogo(toolBar.getTvgLogValue());
                    }
                });
            }
        });
    }

    public void setTvLogo(String logoUrl) {
        // 获取当前大小
        ImageDownloadUtil.download(logoUrl, () -> {
            logo.setIcon(Icons.LOADING_TV_LOGO);
        }, httpImage -> {
            logo.setPreferredSize(TvLogoPanel.R.LOGO_LABEL_SIZE);
            logo.setIcon(Icons.shrinkToPanelSize(httpImage, R.LOGO_LABEL_SIZE));
        }, url -> {
            logo.setIcon(Icons.DEFAULT_TV_LOGO);
        });
    }

}
