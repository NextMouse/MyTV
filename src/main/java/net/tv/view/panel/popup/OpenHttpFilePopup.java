package net.tv.view.panel.popup;

import cn.hutool.core.util.StrUtil;
import net.tv.service.PlaylistService;
import net.tv.view.arm.GodHand;
import net.tv.view.component.FieldItem;
import net.tv.view.panel.root.left.GroupListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class OpenHttpFilePopup extends JDialog {

    interface R {
        Dimension DIALOG_SIZE = new Dimension(380, 120);
    }

    private final FieldItem filePathItem;

    private final JButton submitBtn;

    public OpenHttpFilePopup() {
        super(GodHand.<JFrame>get(GodHand.K.WindowMain), "打开网络文件", true);

        setLayout(null);

        this.filePathItem = new FieldItem("地址");

        this.submitBtn = new JButton("确 定");
        this.submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.filePathItem.setBounds(10, 10, 330, 30);
        this.submitBtn.setBounds(145, 45, 80, 30);

        add(this.filePathItem);
        add(this.submitBtn);

        this.filePathItem.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    submitBtn.doClick();
                }
            }
        });

        this.submitBtn.addActionListener(e -> {
            final String filePath = this.filePathItem.getFieldValue();
            if (StrUtil.isBlank(filePath)) return;
            GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, service -> {
                service.readHttp(filePath);
                GodHand.exec(GodHand.K.GroupListPanel, GroupListPanel::showGroupPanel);
            });
        });

        setSize(R.DIALOG_SIZE);
        setResizable(false);
        setLocationRelativeTo(null);
    }


    public void open() {
        this.setVisible(true);
    }

    public void close() {
        this.setVisible(false);
        this.dispose();
    }
}
