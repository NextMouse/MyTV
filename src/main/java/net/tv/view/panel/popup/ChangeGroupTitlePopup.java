package net.tv.view.panel.popup;

import net.tv.service.PlaylistService;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.FieldItem;
import net.tv.view.panel.root.left.GroupListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChangeGroupTitlePopup extends JDialog {

    private final JLabel oldGroupTitleLabel;

    private final FieldItem newGroupTitleLabel;

    private final JButton button;

    interface R {
        Dimension DIALOG_SIZE = new Dimension(320, 180);
    }

    public ChangeGroupTitlePopup() {
        super(GodHand.<JFrame>get(GodHand.K.WindowMain), "更改组名", true);

        setLayout(null);

        oldGroupTitleLabel = new JLabel(" 旧名称：");
        newGroupTitleLabel = new FieldItem("新名称");
        button = new JButton("确定");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        oldGroupTitleLabel.setBounds(10, 10, 280, 35);
        newGroupTitleLabel.setBounds(10, 55, 280, 35);
        button.setBounds(110, 100, 100, 35);

        add(oldGroupTitleLabel);
        add(newGroupTitleLabel);
        add(button);

        newGroupTitleLabel.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });

        setSize(R.DIALOG_SIZE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void open(String oldGroupTitleText) {
        oldGroupTitleLabel.setText(oldGroupTitleLabel.getText() + oldGroupTitleText);
        button.addActionListener(e -> {
            try {
                PlaylistService playlistService = GodHand.get(GodHand.K.PlaylistService);
                final String newGroupTitle = newGroupTitleLabel.getFieldValue();
                int count = playlistService.updateGroupTitle(oldGroupTitleText, newGroupTitle);
                ConsoleLog.println("修改成功，新名称：{}，共{}个", newGroupTitle, count);
                GroupListPanel groupListPanel = GodHand.get(GodHand.K.GroupListPanel);
                groupListPanel.refresh(newGroupTitle);
            } catch (Exception ex) {
                ConsoleLog.println("失败：{}", ex.getMessage());
            }
            close();
        });
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

}
