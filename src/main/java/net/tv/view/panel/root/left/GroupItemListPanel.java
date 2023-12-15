package net.tv.view.panel.root.left;

import cn.hutool.core.collection.CollectionUtil;
import net.tv.service.PlaylistService;
import net.tv.service.model.PlayViewItem;
import net.tv.view.arm.GodHand;
import net.tv.view.component.CustomizeComponent;
import net.tv.view.component.Icons;
import net.tv.view.component.ScrollListAndTitlePanel;
import net.tv.view.panel.root.center.VideoManagerToolBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class GroupItemListPanel extends ScrollListAndTitlePanel<GroupItemListPanel.ItemPanel> {

    public void clear() {
        customizeList.clear();
    }

    interface R {
        String TITLE = "电视台";
    }

    public GroupItemListPanel() {
        super(R.TITLE, 150, itemPanel -> {
            if (itemPanel == null) return;
            GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, videoToolBar -> videoToolBar.setPlayViewItem(itemPanel.playViewItem));
        });
        // 添加组件
        GodHand.register(GodHand.K.GroupItemListPanel, this);
    }

    public void setDataByMyPlayItem(List<PlayViewItem> playViewItemList) {
        List<ItemPanel> itemPanelList = playViewItemList.stream().map(ItemPanel::new).collect(Collectors.toList());
        this.customizeList.setData(itemPanelList);
    }

    public void refresh(String groupTitle) {
        GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> {
            List<PlayViewItem> playViewItemList = playlistService.getChannelTitleList(groupTitle);
            if (CollectionUtil.isNotEmpty(playViewItemList)) {
                setDataByMyPlayItem(playViewItemList);
            }
        });
        customizeList.setSelectedIndex(0);
        GroupItemListPanel.ItemPanel itemPanel = customizeList.getModel(0);
        if (itemPanel == null) return;
        GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, videoToolBar -> videoToolBar.setPlayViewItem(itemPanel.playViewItem));
    }

    public void refresh(PlayViewItem playViewItem) {
        refresh(playViewItem.getGroupTitle());
    }

    @Override
    public CustomizeComponent getCustomizeComponent() {
        return new CustomizeComponent<ItemPanel>() {
            @Override
            public void onMouseMoved(JList jList, ItemPanel comp) {
                comp.tvTitleLabel.setForeground(R.MOVED_FOREGROUND);
                comp.setBackground(R.MOVED_BACKGROUND);
                comp.setForeground(R.MOVED_FOREGROUND);
            }

            @Override
            public void onDefault(JList<ItemPanel> jList, ItemPanel comp) {
                comp.tvTitleLabel.setForeground(R.DEFAULT_FOREGROUND);
                comp.setForeground(R.DEFAULT_FOREGROUND);
                comp.setBackground(null);
            }
        };
    }

    public class ItemPanel extends JPanel {

        interface R {
            Dimension BTN_SIZE = new Dimension(30, 30);
            Insets BTN_MARGIN = new Insets(3, 3, 5, 3);
        }

        /**
         * 原始数据
         */
        private final PlayViewItem playViewItem;

        private final JLabel tvTitleLabel;

        public PlayViewItem getPlayViewItem() {
            return playViewItem;
        }

        public ItemPanel(PlayViewItem playViewItem) {
            this.playViewItem = playViewItem;
            // 电视台名称
            tvTitleLabel = new JLabel(playViewItem.getChannelTitle());
            // 加心收藏
            JButton favoriteBtn = new JButton();
            favoriteBtn.setIcon(playViewItem.getFavorite() ? Icons.Standard.LIKE : Icons.Standard.DISLIKE);
            favoriteBtn.setPreferredSize(R.BTN_SIZE);
            favoriteBtn.setMargin(R.BTN_MARGIN);
            favoriteBtn.setBackground(null);

            // 设置布局
            setLayout(new BorderLayout());

            // 添加组件
            add(favoriteBtn, BorderLayout.WEST);
            add(tvTitleLabel, BorderLayout.CENTER);

            setBackground(null);
        }

        public void like() {
            playViewItem.setFavorite(true);
            GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> {
                playlistService.addOrUpdate(playViewItem);
            });
            refresh(playViewItem);
        }

        public void disLike() {
            playViewItem.setFavorite(false);
            GodHand.<PlaylistService>exec(GodHand.K.PlaylistService, playlistService -> {
                playlistService.addOrUpdate(playViewItem);
            });
            refresh(playViewItem);
        }

        public boolean favorite() {
            return playViewItem.getFavorite();
        }


    }


}
