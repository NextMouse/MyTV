package net.tv.view.panel.root.top;

import net.tv.view.WindowMain;
import net.tv.view.arm.GodHand;
import net.tv.view.component.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class TopTitlePanel extends JToolBar {

    private JLabel titleLabel;
    private Point mousePoint;

    interface R {
        Insets MARGIN = new Insets(0, 1, 0, 2);
        String TITLE = " Media Play Manager For m3u";
        Color TITLE_COLOR = Color.decode("#F3EEEA");
        Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    }

    private List<JComponent> componentList = new ArrayList<>();

    {
        titleLabel = new JLabel(R.TITLE, Icons.TITLE_LOGO, SwingConstants.LEFT);
        titleLabel.setSize(titleLabel.getWidth(), 30);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(R.TITLE_FONT);
        componentList.add(titleLabel);
    }

    public TopTitlePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setMargin(R.MARGIN);
        setFloatable(false);
        for (JComponent component : componentList) {
            add(component);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePoint = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                WindowMain windowMain = GodHand.get(GodHand.K.WindowMain);
                if (windowMain.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    return;
                }
                windowMain.setLocation(new Point(e.getXOnScreen() - mousePoint.x, e.getYOnScreen() - mousePoint.y));
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                GodHand.<WindowMain>exec(GodHand.K.WindowMain, windowMain -> windowMain.setCursor(Cursor.getDefaultCursor()));
            }
        });
    }

}
