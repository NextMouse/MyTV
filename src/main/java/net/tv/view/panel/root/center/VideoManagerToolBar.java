package net.tv.view.panel.root.center;

import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.util.StringUtils;
import net.tv.service.PlaylistService;
import net.tv.service.model.PlayViewItem;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.FieldItem;
import net.tv.view.component.Icons;
import net.tv.view.component.MediaPlayerManager;
import net.tv.view.component.SimpleButton;
import net.tv.view.panel.root.left.GroupItemListPanel;
import net.tv.view.panel.root.left.GroupListPanel;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.REMAINDER;

public class VideoManagerToolBar extends JPanel {

    private final TvLogoPanel logoPanel = new TvLogoPanel();
    private final FieldItem groupTitleItem = new FieldItem("group-title");
    private final FieldItem titleItem = new FieldItem("title");
    private final FieldItem durationItem = new FieldItem("duration");
    private final FieldItem tvgIdItem = new FieldItem("tvg-id");
    private final FieldItem tvgNameItem = new FieldItem("tvg-name");
    private final FieldItem tvgLogoItem = new FieldItem("tvg-logo");
    private final FieldItem urlItem = new FieldItem("url");
    private final JTextArea consoleLogTextArea = new JTextArea();
    private final JSlider volumeSlider = new JSlider(0, 100);

    private String lastGroupTitle;

    interface R {
        Insets MARGIN = new Insets(0, 0, 0, 0);
        String CONSOLE_PREFIX = "控制台：";
        int CONSOLE_TEXTAREA_HEIGHT = 120;
    }

    public VideoManagerToolBar() {

        setLayout(new BorderLayout());

        // 第一层 工具按钮
        JToolBar videoInfoToolBar = new JToolBar();
        videoInfoToolBar.add(new JLabel(R.CONSOLE_PREFIX));
        JPanel videoToolBarPanel = new JPanel();
        videoToolBarPanel.setLayout(new BorderLayout());
        videoToolBarPanel.add(videoInfoToolBar, BorderLayout.CENTER);
        videoToolBarPanel.add(getVideoToolBar(), BorderLayout.EAST);
        add(videoToolBarPanel, BorderLayout.NORTH);

        // 第二层详细信息
        add(logoPanel, BorderLayout.WEST);
        add(getAttrPanel(), BorderLayout.CENTER);

        // 第三层日志层
        consoleLogTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(-1, R.CONSOLE_TEXTAREA_HEIGHT));
        scrollPane.setViewportView(consoleLogTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JPanel getAttrPanel() {
        JPanel attrPanel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        attrPanel.setLayout(gridBagLayout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = BOTH;

        constraints.gridwidth = BOTH;
        gridBagLayout.setConstraints(groupTitleItem, constraints);

        groupTitleItem.add(new SimpleButton(Icons.Standard.LIGHTNING, "使用上一个组名", (e, btn) -> {
            if (StrUtil.isNotBlank(lastGroupTitle)) {
                groupTitleItem.setFieldValue(lastGroupTitle);
            }
        }), BorderLayout.EAST);
        attrPanel.add(groupTitleItem);

        constraints.gridwidth = REMAINDER;
        gridBagLayout.setConstraints(titleItem, constraints);
        attrPanel.add(titleItem);

        constraints.gridwidth = BOTH;
        gridBagLayout.setConstraints(durationItem, constraints);
        attrPanel.add(durationItem);

        constraints.gridwidth = BOTH;
        gridBagLayout.setConstraints(tvgIdItem, constraints);
        attrPanel.add(tvgIdItem);

        constraints.gridwidth = REMAINDER;
        gridBagLayout.setConstraints(tvgNameItem, constraints);
        attrPanel.add(tvgNameItem);

        constraints.gridwidth = REMAINDER;
        gridBagLayout.setConstraints(tvgLogoItem, constraints);
        attrPanel.add(tvgLogoItem);

        constraints.gridwidth = REMAINDER;
        gridBagLayout.setConstraints(urlItem, constraints);
        attrPanel.add(urlItem);

        return attrPanel;
    }

    public void setVolumeSliderEnabled(boolean enabled) {
        volumeSlider.setEnabled(enabled);
    }

    private JToolBar getVideoToolBar() {
        JToolBar videoToolBar = new JToolBar();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        videoToolBar.setMargin(R.MARGIN);
        videoToolBar.setLayout(flowLayout);

        videoToolBar.add(new SimpleButton("刷新", Icons.Standard.VIDEO_REFRESH, (e, btn) -> GodHand.exec(GodHand.K.MediaPlayerManager, MediaPlayerManager::refresh)));

        videoToolBar.add(new SimpleButton("声音：", Icons.Standard.VIDEO_VOLUME_OPEN, (e, btn) -> {
            MediaPlayerManager playerManager = GodHand.get(GodHand.K.MediaPlayerManager);
            if (playerManager.getMediaPlayer() == null) {
                ConsoleLog.println("当前没有媒体载入");
                return;
            }
            if ("已静音".equals(btn.getText())) { // 准备打开
                btn.setText("");
                btn.setIcon(Icons.Standard.VIDEO_VOLUME_OPEN);
                volumeSlider.setVisible(true);
                playerManager.setSilent(false);
            } else { // 准备静音
                btn.setText("已静音");
                btn.setIcon(Icons.Standard.VIDEO_VOLUME_CLOSE);
                volumeSlider.setVisible(false);
                playerManager.setSilent(true);
            }
        }));

        volumeSlider.setValue(100);
        volumeSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setVolumeSliderEnabled(false);
        volumeSlider.addChangeListener(e -> {
            JSlider vp = (JSlider) e.getSource();
            MediaPlayerManager playerManager = GodHand.get(GodHand.K.MediaPlayerManager);
            playerManager.setVolume(vp.getValue());
        });
        videoToolBar.add(volumeSlider);

        videoToolBar.add(new SimpleButton("播放中", Icons.Standard.VIDEO_PLAY, (e, btn) -> {
            MediaPlayerManager playerManager = GodHand.get(GodHand.K.MediaPlayerManager);
            if (playerManager.stateInfo().playStatus) {
                playerManager.pause();
                btn.setText("暂停中");
                btn.setIcon(Icons.Standard.VIDEO_PAUSE);
            } else {
                playerManager.play();
                btn.setText("播放中");
                btn.setIcon(Icons.Standard.VIDEO_PLAY);
            }
        }));

        videoToolBar.add(new SimpleButton("新增", Icons.Standard.FILE_SAVE, (e, btn) -> {
            PlayViewItem playViewItem = getPlayViewItem(null);
            if (playViewItem == null) return;
            lastGroupTitle = groupTitleItem.getFieldValue();
            GroupItemListPanel groupItemListPanel = GodHand.get(GodHand.K.GroupItemListPanel);
            GroupItemListPanel.ItemPanel itemPanel = groupItemListPanel.customizeList.getSelectedValue();
            GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> playlistService.addOrUpdate(playViewItem));
            refresh(groupItemListPanel, itemPanel);
        }));

        videoToolBar.add(new SimpleButton("更新", Icons.Standard.FILE_SAVE, (e, btn) -> {
            PlayViewItem playViewItem = getPlayViewItem(null);
            if (playViewItem == null) return;
            lastGroupTitle = groupTitleItem.getFieldValue();
            GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, groupItemListPanel -> {
                GroupItemListPanel.ItemPanel itemPanel = groupItemListPanel.customizeList.getSelectedValue();
                if (itemPanel == null) return;
                playViewItem.setId(itemPanel.getPlayViewItem().getId());
                GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> playlistService.addOrUpdate(playViewItem));
                refresh(groupItemListPanel, itemPanel);
            });
        }));

        videoToolBar.add(new SimpleButton("删除", Icons.Standard.FILE_DELETE, (e, btn) -> {
            GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, groupItemListPanel -> {
                GroupItemListPanel.ItemPanel itemPanel = groupItemListPanel.customizeList.getSelectedValue();
                if (itemPanel == null) return;
                GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> {
                    if (itemPanel.getPlayViewItem() == null) return;
                    playlistService.delete(itemPanel.getPlayViewItem());
                });
                refresh(groupItemListPanel, itemPanel);
            });
        }));

        videoToolBar.add(new SimpleButton("可用", Icons.Standard.LIKE, (e, btn) -> {
            GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, groupItemListPanel -> {
                GroupItemListPanel.ItemPanel itemPanel = groupItemListPanel.customizeList.getSelectedValue();
                if (itemPanel == null) return;
                if (itemPanel.favorite()) itemPanel.disLike();
                else itemPanel.like();
            });
        }));

        return videoToolBar;
    }

    public void refresh(GroupItemListPanel itemListPanel, GroupItemListPanel.ItemPanel itemPanel) {
        final String groupTitle = itemPanel.getPlayViewItem().getGroupTitle();
        itemListPanel.refresh(itemPanel.getPlayViewItem());
        GroupListPanel groupPanel = GodHand.get(GodHand.K.GroupListPanel);
        groupPanel.refresh(groupTitle);
    }

    public void setPlayViewItem(PlayViewItem playViewItem) {
        GodHand.<JTextField>exec(GodHand.K.MediaLinkTextFiled, mediaLink -> mediaLink.setCaretPosition(0));
        groupTitleItem.setFieldValue(playViewItem.getGroupTitle());
        titleItem.setFieldValue(playViewItem.getChannelTitle());
        if (playViewItem.getDuration() != null) {
            durationItem.setFieldValue(String.valueOf(playViewItem.getDuration()));
        }
        tvgIdItem.setFieldValue(playViewItem.getTvgId());
        tvgNameItem.setFieldValue(playViewItem.getTvgName());
        tvgLogoItem.setFieldValue(playViewItem.getTvgLogo());
        urlItem.setFieldValue(playViewItem.getMediaUrl());
        if (StrUtil.isNotBlank(playViewItem.getTvgLogo())) {
            logoPanel.setTvLogo(playViewItem.getTvgLogo());
        }
        GodHand.<MediaPlayerManager>exec(GodHand.K.MediaPlayerManager, playerManager -> {
            String mediaUrl = playViewItem.getMediaUrl();
            if (StrUtil.isNotBlank(mediaUrl)) {
                playerManager.load(mediaUrl).play();
            }
        });
    }

    public PlayViewItem getPlayViewItem(String id) {
        PlayViewItem playViewItem = PlayViewItem.builder().id(id).groupTitle(groupTitleItem.getFieldValue()).channelTitle(titleItem.getFieldValue()).duration(StringUtils.isEmpty(durationItem.getFieldValue()) ? -1 : Integer.parseInt(durationItem.getFieldValue())).tvgId(tvgIdItem.getFieldValue()).tvgName(tvgNameItem.getFieldValue()).tvgLogo(tvgLogoItem.getFieldValue()).aspectRatio(null).mediaUrl(urlItem.getFieldValue()).build();
        if (StrUtil.isBlank(playViewItem.getChannelTitle()) || StrUtil.isBlank(playViewItem.getMediaUrl())) {
            return null;
        }
        return playViewItem;
    }

    public void appendConsoleLog(String log) {
        consoleLogTextArea.append(log);
        consoleLogTextArea.setCaretPosition(consoleLogTextArea.getDocument().getLength());
    }

}
