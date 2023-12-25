package net.tv.view.panel.popup;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import net.tv.view.WindowMain;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.FieldItem;
import net.tv.view.config.ProxyConfig;
import net.tv.view.config.SystemConfig;
import net.tv.view.config.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class ConfigPopup extends JDialog {

    private final FieldItem openDirPath;

    private JComboBox<String> themeCombo;

    private final FieldItem proxyHostname;

    private final FieldItem proxyPort;

    private final JTextArea tvSourceArea;

    interface R {
        Dimension DIALOG_SIZE = new Dimension(450, 280);
    }

    public ConfigPopup() {
        super(GodHand.<JFrame>get(GodHand.K.WindowMain), "系统配置", true);

        openDirPath = new FieldItem("默认打开文件夹");
        proxyHostname = new FieldItem("图片加载代理地址");
        proxyPort = new FieldItem("图片加载代理端口");

        JPanel root = new JPanel();

        root.setLayout(new GridLayout(5, 1, 5, 5));

        root.add(getThemeBox());
        root.add(openDirPath);
        root.add(proxyHostname);
        root.add(proxyPort);
        root.add(new JLabel(" m3u静态源列表："));
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
        ConsoleLog.println("当前主题：{}", themeName);
        themeCombo.setSelectedItem(themeName);
        openDirPath.setFieldValue(systemConfig.getOpenDirPath());
        proxyHostname.setFieldValue(systemConfig.getTvgLogo().getHostname());
        proxyPort.setFieldValue(systemConfig.getTvgLogo().getPort());
        if (CollectionUtil.isNotEmpty(systemConfig.getTvSources())) {
            tvSourceArea.setText(String.join(System.lineSeparator(), systemConfig.getTvSources()));
        }
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
        systemConfig.save();
    }


    public String getThemeSelected() {
        Object selectedItem = themeCombo.getSelectedItem();
        return selectedItem != null ? selectedItem.toString() : null;
    }
}
