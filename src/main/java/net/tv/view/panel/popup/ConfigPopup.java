package net.tv.view.panel.popup;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import net.tv.view.WindowMain;
import net.tv.view.arm.GodHand;
import net.tv.view.component.FieldItem;
import net.tv.view.component.impl.JavaFxPlayer;
import net.tv.view.component.impl.VlcPlayer;
import net.tv.view.config.ProxyConfig;
import net.tv.view.config.SystemConfig;
import net.tv.view.config.ThemeConfig;
import net.tv.view.panel.root.center.VideoPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import static net.tv.view.component.impl.MediaPlayerProxy.MediaPlayerType;

public class ConfigPopup extends JDialog {

    private final FieldItem openDirPath;

    private JComboBox<String> themeCombo;

    private FieldItem proxyHostname;

    private FieldItem proxyPort;

    private final JTextArea tvSourceArea;
    private JRadioButton vlcRadio;
    private JRadioButton javaFxRadio;

    private MediaPlayerType selectedMediaPlayerType;
    private MediaPlayerType initMediaPlayerType;

    interface R {
        Dimension DIALOG_SIZE = new Dimension(450, 280);
    }

    public ConfigPopup() {
        super(GodHand.<JFrame>get(GodHand.K.WindowMain), "系统配置", true);

        openDirPath = new FieldItem("默认打开文件夹");

        JPanel root = new JPanel();

        root.setLayout(new GridLayout(5, 1, 3, 5));

        root.add(getThemeBox());
        root.add(getMediaRadio());
        root.add(openDirPath);
        root.add(getProxyBox());
        root.add(new JLabel(" m3u资源地址列表："));
        add(root, BorderLayout.NORTH);

        tvSourceArea = new JTextArea();
        tvSourceArea.setSize(getWidth(), 300);
        JScrollPane areaScrollPane = new JScrollPane(tvSourceArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, // 始终显示纵向滚动条
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); // 始终显示横向滚动条
        add(areaScrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        setSize(R.DIALOG_SIZE);
        setResizable(false);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private JPanel getMediaRadio() {
        this.vlcRadio = new JRadioButton("VLC Player");
        this.javaFxRadio = new JRadioButton("JavaFX");
        this.javaFxRadio.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(vlcRadio);
        group.add(javaFxRadio);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(" 播放器："));
        panel.add(vlcRadio);
        panel.add(javaFxRadio);
        return panel;
    }

    private JPanel getProxyBox() {
        this.proxyHostname = new FieldItem("图片代理", "代理地址");
        this.proxyPort = new FieldItem(null, "端口号");
        this.proxyPort.setPreferredSize(new Dimension(100, this.proxyPort.getHeight()));
        this.proxyHostname.add(this.proxyPort, BorderLayout.EAST);
        return this.proxyHostname;
    }

    private JPanel getThemeBox() {
        JPanel themeBox = new JPanel();
        themeBox.setLayout(new BorderLayout());
        themeBox.add(new JLabel(" 系统主题："), BorderLayout.WEST);

        themeCombo = new JComboBox<>();
        for (ThemeConfig.Key key : ThemeConfig.Key.values()) {
            themeCombo.addItem(key.toString());
        }

        themeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                WindowMain.initTheme(ThemeConfig.Key.getOrDefault(getThemeSelected()));
            }
        });

        themeBox.add(themeCombo, BorderLayout.CENTER);
        return themeBox;
    }

    public void open() {
        SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
        String themeName = systemConfig.getTheme().getSystem().toString();
        themeCombo.setSelectedItem(themeName);
        openDirPath.setFieldValue(systemConfig.getOpenDirPath());
        proxyHostname.setFieldValue(systemConfig.getTvgLogo().getHostname());
        proxyPort.setFieldValue(systemConfig.getTvgLogo().getPort());
        if (CollectionUtil.isNotEmpty(systemConfig.getTvSources())) {
            tvSourceArea.setText(String.join(System.lineSeparator(), systemConfig.getTvSources()));
            tvSourceArea.setCaretPosition(0);
        }
        selectedMediaPlayerType = systemConfig.getMediaPlayerType();
        if (selectedMediaPlayerType == MediaPlayerType.VLC) {
            vlcRadio.setSelected(true);
        }
        initMediaPlayerType = systemConfig.getMediaPlayerType();
        vlcRadio.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                selectedMediaPlayerType = MediaPlayerType.VLC;
            }
        });
        javaFxRadio.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                selectedMediaPlayerType = MediaPlayerType.JavaFx;
            }
        });
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
        systemConfig.setTvgLogo(ProxyConfig.builder().hostname(proxyHostname.getFieldValue()).port(proxyPort.getFieldValue()).build());
        systemConfig.setTheme(ThemeConfig.builder().system(ThemeConfig.Key.getOrDefault(getThemeSelected())).build());
        String tvSourceText = tvSourceArea.getText();
        if (StrUtil.isNotBlank(tvSourceText)) {
            String[] lines = tvSourceText.split("\n");
            systemConfig.setTvSources(Arrays.stream(lines).filter(StrUtil::isNotBlank).map(String::trim).toList());
        }
        if (selectedMediaPlayerType != initMediaPlayerType) {
            systemConfig.setMediaPlayerType(selectedMediaPlayerType);
            if (selectedMediaPlayerType == MediaPlayerType.VLC) {
                GodHand.<VideoPanel>exec(GodHand.K.VideoPanel, videoPanel -> videoPanel.changeMediaPlayer(new VlcPlayer()));
            } else {
                GodHand.<VideoPanel>exec(GodHand.K.VideoPanel, videoPanel -> videoPanel.changeMediaPlayer(new JavaFxPlayer()));
            }
        }

        systemConfig.save();
        this.dispose();
    }


    public String getThemeSelected() {
        Object selectedItem = themeCombo.getSelectedItem();
        return selectedItem != null ? selectedItem.toString() : null;
    }

}
