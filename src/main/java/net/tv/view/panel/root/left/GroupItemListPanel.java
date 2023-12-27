package net.tv.view.panel.root.left;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;
import net.tv.service.PlaylistService;
import net.tv.service.model.PlayViewItem;
import net.tv.util.GIFResizer;
import net.tv.util.ImageDownloadUtil;
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
            GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, videoToolBar -> {
                videoToolBar.setPlayViewItem(itemPanel.playViewItem);
            });
        });
        // 添加组件
        GodHand.register(GodHand.K.GroupItemListPanel, this);
    }

    private void setDataByMyPlayItem(List<PlayViewItem> playViewItemList) {
        List<ItemPanel> itemPanelList = playViewItemList.stream().map(ItemPanel::new).collect(Collectors.toList());
        this.customizeList.setData(itemPanelList);
    }

    public void refresh(String groupTitle, int selectIndex) {
        final int finalSelectIndex = selectIndex >= customizeList.getModel().getSize() ? 0 : selectIndex;
        GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, itemPanel -> {
            PlaylistService service = GodHand.get(GodHand.K.PlaylistService);
            List<PlayViewItem> playViewItemList = service.getChannelTitleList(groupTitle);
            if (CollectionUtil.isNotEmpty(playViewItemList)) {
                itemPanel.setDataByMyPlayItem(playViewItemList);
                this.customizeList.setSelectedIndex(finalSelectIndex);
            }
        });
    }

    @Override
    public CustomizeComponent<ItemPanel> getCustomizeComponent() {
        return new CustomizeComponent<>() {
            @Override
            public void onMouseMoved(JList<ItemPanel> jList, ItemPanel comp) {
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

    public static class ItemPanel extends JPanel {

        interface R {
            Dimension LOGO_SIZE = new Dimension(50, 40);
            Dimension LOADING_SIZE = new Dimension(35, 35);
        }

        /**
         * 原始数据
         */
        @Getter
        private final PlayViewItem playViewItem;

        private final JLabel tvTitleLabel;

        public ItemPanel(PlayViewItem playViewItem) {
            this.playViewItem = playViewItem;

            // 电台图标
            final JLabel tvLogo = getTvLogoLabel(playViewItem.getTvgLogo());

            // 设置布局
            setLayout(new BorderLayout());

            // 添加组件
            add(tvLogo, BorderLayout.WEST);

            // 电视台名称
            tvTitleLabel = new JLabel(playViewItem.getChannelTitle());
            add(tvTitleLabel, BorderLayout.CENTER);

            setBackground(null);
        }

        private JLabel getTvLogoLabel(String logoUrl) {
            JLabel tvLogo = new JLabel();
            ImageDownloadUtil.download(logoUrl,
                    () -> tvLogo.setIcon(GIFResizer.resize(Icons.LOADING_TV_LOGO, R.LOADING_SIZE)),
                    imageIcon -> tvLogo.setIcon(Icons.shrinkToPanelSize(imageIcon, R.LOGO_SIZE)),
                    (url) -> tvLogo.setIcon(Icons.Standard.TV));
            tvLogo.setHorizontalAlignment(SwingConstants.CENTER);
            tvLogo.setPreferredSize(R.LOGO_SIZE);
            tvLogo.setBackground(null);
            return tvLogo;
        }


    }

}
