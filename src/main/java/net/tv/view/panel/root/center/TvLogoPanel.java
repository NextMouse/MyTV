package net.tv.view.panel.root.center;

import net.tv.util.ImageDownloadUtil;
import net.tv.view.component.Icons;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

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
    }

    public void setTvLogo(String logoUrl) {
        logo.setIcon(Icons.LOADING_TV_LOGO);
        // 获取当前大小
        ImageDownloadUtil.download(logoUrl, httpImage -> {
            double coefficient = 0.9;
            // 获取图片宽高
            Dimension imageSize = getSuitableSize(0.9, httpImage.getIconWidth(), httpImage.getIconHeight());
            while (!(imageSize.width < R.LOGO_LABEL_SIZE.width && imageSize.height < R.LOGO_LABEL_SIZE.height) && coefficient > 0.5) {
                coefficient = coefficient - 0.05;
                imageSize = getSuitableSize(coefficient, httpImage.getIconWidth(), httpImage.getIconHeight());
            }
            logo.setPreferredSize(R.LOGO_LABEL_SIZE);
            logo.setIcon(new ImageIcon(httpImage.getImage()
                    .getScaledInstance(imageSize.width, imageSize.height,
                            Image.SCALE_SMOOTH)));
        }, url -> {
            logo.setIcon(Icons.DEFAULT_TV_LOGO);
        });
    }

    public Dimension getSuitableSize(double coefficient, int imageWidth, int imageHeight) {
        int logoLabelWidth = R.LOGO_LABEL_SIZE.width;
        int logoLabelHeight = R.LOGO_LABEL_SIZE.height;
        double showWidth = logoLabelWidth * coefficient;
        double showHeight = logoLabelHeight * coefficient;
        if (imageWidth > imageHeight) {
            double proportion = showWidth / imageWidth;
            showHeight = imageHeight * proportion;
        } else {
            double proportion = showHeight / imageHeight;
            showWidth = imageWidth * proportion;
        }
        return new Dimension((int) showWidth, (int) showHeight);
    }

}
