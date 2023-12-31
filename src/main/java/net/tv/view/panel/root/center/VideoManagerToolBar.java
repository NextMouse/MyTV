package net.tv.view.panel.root.center;

import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.util.StringUtils;
import lombok.Getter;
import net.tv.m3u.M3uParser;
import net.tv.service.PlaylistService;
import net.tv.service.model.PlayViewItem;
import net.tv.util.AsyncUtil;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.FieldItem;
import net.tv.view.component.IMediaPlayer;
import net.tv.view.component.Icons;
import net.tv.view.component.SimpleButton;
import net.tv.view.component.impl.MediaPlayerProxy;
import net.tv.view.panel.root.left.GroupItemListPanel;
import net.tv.view.panel.root.left.GroupListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.List;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.REMAINDER;

@Getter
public class VideoManagerToolBar extends JPanel {

    private final TvLogoPanel logoPanel = new TvLogoPanel();
    private final FieldItem groupTitleItem = new FieldItem("分组名称");
    private final FieldItem titleItem = new FieldItem("节目名称");
    private final FieldItem durationItem = new FieldItem("持续时间");
    private final FieldItem tvgIdItem = new FieldItem("台标编号");
    private final FieldItem tvgNameItem = new FieldItem("台标名称");
    private final FieldItem tvgLogoItem = new FieldItem("台标图片");
    private final FieldItem urlItem = new FieldItem("节目地址");
    private final JTextArea consoleLogTextArea = new JTextArea();
    private final JSlider volumeSlider = new JSlider(0, 100);

    private String lastGroupTitle;

    interface R {
        Insets MARGIN = new Insets(0, 0, 0, 0);
        String CONSOLE_PREFIX = "控制台：";
        int CONSOLE_TEXTAREA_HEIGHT = 80;
    }

    public VideoManagerToolBar() {

        setLayout(new BorderLayout());

        // URL增加播放按钮
        urlItem.add(new SimpleButton(Icons.Standard.VIDEO_PLAY, "点击播放", (e, btn) -> {
            final String mediaSrc = urlItem.getFieldValue();
            if (StrUtil.isNotBlank(mediaSrc)) {
                GodHand.<MediaPlayerProxy>exec(GodHand.K.MediaPlayerProxy, player -> player.play(mediaSrc));
            }
        }), BorderLayout.EAST);


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

    private JToolBar getVideoToolBar() {
        JToolBar videoToolBar = new JToolBar();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        videoToolBar.setMargin(R.MARGIN);
        videoToolBar.setLayout(flowLayout);
        videoToolBar.setFloatable(false);
        videoToolBar.setBorderPainted(false);

        videoToolBar.add(new SimpleButton("", Icons.Standard.VIDEO_VOLUME_OPEN, (e, btn) -> {
            MediaPlayerProxy mediaPlayer = GodHand.get(GodHand.K.MediaPlayerProxy);
            if ("已静音".equals(btn.getText())) { // 准备打开
                btn.setText("");
                btn.setIcon(Icons.Standard.VIDEO_VOLUME_OPEN);
                volumeSlider.setVisible(true);
                volumeSlider.setEnabled(true);
                mediaPlayer.mute(false);
            } else { // 准备静音
                btn.setText("已静音");
                btn.setIcon(Icons.Standard.VIDEO_VOLUME_CLOSE);
                volumeSlider.setVisible(false);
                mediaPlayer.mute(true);
            }
        }));

        volumeSlider.setValue(100);
        volumeSlider.setPreferredSize(new Dimension(80, 20));
        volumeSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        volumeSlider.setEnabled(false);
        volumeSlider.addChangeListener(e -> {
            JSlider slider = (JSlider) e.getSource();
            GodHand.<MediaPlayerProxy>exec(GodHand.K.MediaPlayerProxy, player -> player.volume(slider.getValue()));
        });
        videoToolBar.add(volumeSlider);

        videoToolBar.add(new SimpleButton("播放中", Icons.Standard.VIDEO_PLAY, (e, btn) -> {
            MediaPlayerProxy player = GodHand.get(GodHand.K.MediaPlayerProxy);
            if (player.getStatus() == IMediaPlayer.Status.PLAYING) {
                player.pause(true);
                btn.setText("暂停中");
                btn.setIcon(Icons.Standard.VIDEO_PAUSE);
            } else {
                player.pause(false);
                btn.setText("播放中");
                btn.setIcon(Icons.Standard.VIDEO_PLAY);
            }
        }));

        videoToolBar.add(new SimpleButton("刷新", Icons.Standard.VIDEO_REFRESH, (e, btn) -> GodHand.exec(GodHand.K.MediaPlayerProxy, MediaPlayerProxy::refresh)));

        videoToolBar.addSeparator();

        videoToolBar.add(new SimpleButton("新增", Icons.Standard.ADD, (e, btn) -> {
            PlayViewItem playViewItem = getPlayViewItem(null);
            if (playViewItem == null) return;
            lastGroupTitle = groupTitleItem.getFieldValue();
            GroupItemListPanel groupItemListPanel = GodHand.get(GodHand.K.GroupItemListPanel);
            GroupItemListPanel.ItemPanel itemPanel = groupItemListPanel.customizeList.getSelectedValue();
            GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> playlistService.addOrUpdate(playViewItem));
            refresh(itemPanel);
        }));

        videoToolBar.add(new SimpleButton("更新", Icons.Standard.UPDATE, (e, btn) -> {
            PlayViewItem playViewItem = getPlayViewItem(null);
            if (playViewItem == null) return;
            lastGroupTitle = groupTitleItem.getFieldValue();
            GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, groupItemListPanel -> {
                GroupItemListPanel.ItemPanel itemPanel = groupItemListPanel.customizeList.getSelectedValue();
                if (itemPanel == null) return;
                playViewItem.setId(itemPanel.getPlayViewItem().getId());
                GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> playlistService.addOrUpdate(playViewItem));
                refresh(itemPanel);
            });
        }));

        videoToolBar.add(new SimpleButton("删除", Icons.Standard.DELETE, (e, btn) -> GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, groupItemListPanel -> {
            GroupItemListPanel.ItemPanel itemPanel = groupItemListPanel.customizeList.getSelectedValue();
            if (itemPanel == null) return;
            GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> {
                if (itemPanel.getPlayViewItem() == null) return;
                playlistService.delete(itemPanel.getPlayViewItem());
            });
            refresh(itemPanel);
        })));

        videoToolBar.addSeparator();

        videoToolBar.add(new SimpleButton("清空", (e, btn) -> setPlayViewItem(new PlayViewItem())));

        videoToolBar.add(new SimpleButton("复制", (e, btn) -> {
            try {
                PlayViewItem playViewItem = getPlayViewItem(null);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(playViewItem.getPlayItem().toString()), null);
                ConsoleLog.println("复制成功！");
            } catch (Exception ex) {
                // ignore
            }
        }));

        videoToolBar.add(new SimpleButton("粘贴", (e, btn) -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // 判断剪切板中是否包含字符串类型的数据
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                // 获取剪切板内容
                Transferable contents = clipboard.getContents(null);
                // 将内容转换为字符串类型并输出
                try {
                    String objStr = contents.getTransferData(DataFlavor.stringFlavor).toString();
                    List<String> lines = Arrays.asList(objStr.split("\n"));
                    PlayViewItem playViewItem = PlayViewItem.getByPlayItem(new M3uParser().parseOne(lines));
                    setPlayViewItem(playViewItem, false);
                    ConsoleLog.println("粘贴成功！");
                } catch (Exception ex) {
                    // ignore
                }
            }
        }));

        return videoToolBar;
    }

    public void refresh(GroupItemListPanel.ItemPanel itemPanel) {
        GodHand.<GroupListPanel>exec(GodHand.K.GroupListPanel, groupListPanel -> {
            final String groupTitle = itemPanel.getPlayViewItem().getGroupTitle();
            groupListPanel.refresh(groupTitle);
        });
    }

    public void setPlayViewItem(PlayViewItem playViewItem) {
        setPlayViewItem(playViewItem, true);
    }

    public void setPlayViewItem(PlayViewItem playViewItem, boolean autoplay) {
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
        if (!autoplay) return;
        AsyncUtil.exec(() -> GodHand.<MediaPlayerProxy>exec(GodHand.K.MediaPlayerProxy, playerManager -> playerManager.play(playViewItem.getMediaUrl())));
    }

    public PlayViewItem getPlayViewItem(String id) {
        PlayViewItem playViewItem = PlayViewItem.builder()
                .id(id).groupTitle(groupTitleItem.getFieldValue())
                .channelTitle(titleItem.getFieldValue())
                .duration(StringUtils.isEmpty(durationItem.getFieldValue()) ? -1 : Integer.parseInt(durationItem.getFieldValue()))
                .tvgId(tvgIdItem.getFieldValue()).tvgName(tvgNameItem.getFieldValue()).tvgLogo(tvgLogoItem.getFieldValue())
                .aspectRatio(null)
                .mediaUrl(urlItem.getFieldValue())
                .build();
        if (StrUtil.isBlank(playViewItem.getChannelTitle()) || StrUtil.isBlank(playViewItem.getMediaUrl())) {
            return null;
        }
        return playViewItem;
    }

    public void appendConsoleLog(String log) {
        consoleLogTextArea.append(log);
        consoleLogTextArea.setCaretPosition(consoleLogTextArea.getDocument().getLength());
    }

    public String getTvgLogValue() {
        return tvgLogoItem.getFieldValue();
    }

}
