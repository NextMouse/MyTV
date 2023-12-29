package net.tv.view;

import cn.hutool.core.util.StrUtil;
import net.tv.service.PlaylistService;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.Icons;
import net.tv.view.component.impl.MediaPlayerProxy;
import net.tv.view.config.SystemConfig;
import net.tv.view.config.ThemeConfig;
import net.tv.view.panel.root.RootPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

public class WindowMain extends JFrame {


    private interface R {
        Border DEFAULT_BORDER = new EmptyBorder(1, 1, 1, 1);
    }

    private WindowMain() {

        // 获取屏幕大小
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension((int) (screenSize.getWidth() / 1.5), (int) (screenSize.getHeight() / 1.5)));
        setMinimumSize(new Dimension((int) (screenSize.getWidth() / 4), (int) (screenSize.getHeight() / 4)));
        // 设置位置
        setLocationRelativeTo(null);
        // 设置布局
        setLayout(new BorderLayout());
        // 添加关闭监听
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    GodHand.exec(GodHand.K.SystemConfig, SystemConfig::save);
                    GodHand.exec(GodHand.K.MediaPlayerProxy, MediaPlayerProxy::release);
                } catch (Exception ex) {
                    // ignore
                }
                System.exit(0);
            }
        });
        // 去掉系统窗体
        setUndecorated(true);
        // 设置图标
        setIconImage(Icons.LOGO.getImage());
        addMouseMotionListener(new MouseDraggedSizeListener(this));
        GodHand.register(GodHand.K.WindowMain, this);
    }

    public void open() {
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(R.DEFAULT_BORDER);
        root.add(RootPanel.instance(), BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);
        EventQueue.invokeLater(() -> setVisible(true));
    }

    class MouseDraggedSizeListener extends MouseMotionAdapter {
        private final static int RESIZE_VALUE = 10;
        private final Component comp;

        public MouseDraggedSizeListener(Component comp) {
            this.comp = comp;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // 如果是最大化就不做任何操作
            WindowMain windowMain = GodHand.get(GodHand.K.WindowMain);
            if (windowMain.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                return;
            }
            switch (comp.getCursor().getType()) {
                case Cursor.SE_RESIZE_CURSOR:
                    comp.setSize(new Dimension(e.getX(), e.getY()));
                    break;
                case Cursor.E_RESIZE_CURSOR:
                    comp.setSize(new Dimension(e.getX(), comp.getHeight()));
                    break;
                case Cursor.S_RESIZE_CURSOR:
                    comp.setSize(new Dimension(comp.getWidth(), e.getY()));
                    break;
            }
            windowMain.setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 2, 2));
        }


        @Override
        public void mouseMoved(MouseEvent e) {
            WindowMain windowMain = GodHand.get(GodHand.K.WindowMain);
            if (windowMain.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                return;
            }
            windowMain.setCursor(Cursor.getDefaultCursor());
            if (Math.abs(comp.getWidth() - e.getX()) < RESIZE_VALUE) {
                if (Math.abs(comp.getHeight() - e.getY()) < RESIZE_VALUE) {
                    comp.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else {
                    comp.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                }
            } else if (Math.abs(comp.getHeight() - e.getY()) < RESIZE_VALUE) {
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            }
        }

    }

    private Rectangle maxBounds;

    public Rectangle getMaximizedBounds() {
        return (maxBounds);
    }

    public void setMaximizedBounds(Rectangle maxBounds) {
        this.maxBounds = maxBounds;
        super.setMaximizedBounds(maxBounds);
    }

    public void setExtendedState(int state) {
        if (maxBounds == null && (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            Insets screenInsets = getToolkit().getScreenInsets(getGraphicsConfiguration());
            Rectangle screenSize = getGraphicsConfiguration().getBounds();
            Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x, screenInsets.top + screenSize.y, screenSize.x + screenSize.width - screenInsets.right - screenInsets.left, screenSize.y + screenSize.height - screenInsets.bottom - screenInsets.top);
            super.setMaximizedBounds(maxBounds);
        }
        super.setExtendedState(state);
    }

    public static void run() {
        try {
            initSystemConfig();
        } catch (Exception e) {
            GodHand.register(GodHand.K.SystemConfig, SystemConfig.getDefault());
        }
        final WindowMain mainUI = new WindowMain();
        GodHand.<SystemConfig>exec(GodHand.K.SystemConfig, sc -> initTheme(sc.getTheme().getSystem()));
        mainUI.open();
    }

    public static void initSystemConfig() {
        // 加载配置文件地址
        GodHand.register(GodHand.K.SystemConfig, SystemConfig.load());
        GodHand.register(GodHand.K.PlaylistService, new PlaylistService());
    }


    public static void initTheme(ThemeConfig.Key themeKey) {
        try {
            WindowMain windowUI = GodHand.get(GodHand.K.WindowMain);
            LookAndFeel themeInstance = themeKey.theme;
            String themeClassName = themeKey.className;
            if (themeInstance != null) {
                UIManager.setLookAndFeel(themeInstance);
            } else if (StrUtil.isNotBlank(themeClassName)) {
                UIManager.setLookAndFeel(themeClassName);
            } else {
                UIManager.setLookAndFeel(ThemeConfig.getDefault().getSystem().theme);
            }
            SwingUtilities.updateComponentTreeUI(windowUI);
        } catch (Exception e) {
            ConsoleLog.println("主题设置失败 => {}", e.getMessage());
        }
    }

}
