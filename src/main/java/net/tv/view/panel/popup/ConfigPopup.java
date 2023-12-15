package net.tv.view.panel.popup;

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

public class ConfigPopup extends JDialog {

    private final FieldItem openDirPath;

    private JComboBox<String> themeCombo;

    private final FieldItem proxyHostname;

    private final FieldItem proxyPort;

    interface R {
        Dimension DIALOG_SIZE = new Dimension(400, 180);
    }

    public ConfigPopup() {
        super(GodHand.<JFrame>get(GodHand.K.WindowMain), "系统配置", true);

        openDirPath = new FieldItem("默认打开文件夹：");
        proxyHostname = new FieldItem("图片加载代理地址：");
        proxyPort = new FieldItem("图片加载代理端口：");

        JPanel root = new JPanel();

        root.setLayout(new GridLayout(4, 1, 5, 5));

        root.add(getThemeBox());
        root.add(openDirPath);
        root.add(proxyHostname);
        root.add(proxyPort);
        add(root, BorderLayout.CENTER);

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
        themeBox.add(new JLabel("系统主题："), BorderLayout.WEST);

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
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
        systemConfig.setOpenDirPath(openDirPath.getFieldValue());
        systemConfig.setTvgLogo(ProxyConfig.builder().hostname(proxyHostname.getFieldValue()).port(proxyPort.getFieldValue()).build());
        systemConfig.setTheme(ThemeConfig.builder().system(ThemeConfig.Key.getOrDefault(getThemeSelected())).build());
        systemConfig.save();
    }


    public String getThemeSelected() {
        Object selectedItem = themeCombo.getSelectedItem();
        return selectedItem != null ? selectedItem.toString() : null;
    }
}
