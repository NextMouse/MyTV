package net.tv.view.panel.root.left;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import net.tv.service.PlaylistService;
import net.tv.service.model.PlayViewItem;
import net.tv.view.arm.GodHand;
import net.tv.view.component.Icons;
import net.tv.view.component.ScrollListAndTitlePanel;
import net.tv.view.panel.popup.ChangeGroupTitlePopup;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;

public class GroupListPanel extends ScrollListAndTitlePanel<GroupListPanel.GroupItem> {

    public void refresh(String groupTitle) {
        PlaylistService playlistService = GodHand.get(GodHand.K.PlaylistService);
        List<String> groupTitleList = playlistService.getByGroupTitle();
        if (CollectionUtil.isNotEmpty(groupTitleList)) {
            setMediaGroupList(groupTitleList);
            int lastIndex = ListUtil.lastIndexOf(groupTitleList, s -> s.equals(groupTitle));
            if (lastIndex != -1) {
                customizeList.setSelectedIndex(lastIndex);
                GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, itemPanel -> itemPanel.refresh(groupTitle));
            } else {
                String groupTitle0 = groupTitleList.get(0);
                customizeList.setSelectedIndex(0);
                GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, itemPanel -> itemPanel.refresh(groupTitle0));
            }
        }
    }

    interface R {
        String TITLE = "分组";
    }

    public void setMediaGroupList(List<String> mediaGroupList) {
        customizeList.setData(mediaGroupList, GroupItem::new);
    }

    public void clear() {
        customizeList.clear();
    }

    public GroupListPanel() {
        super(R.TITLE, 120, groupTitleLabel -> {
            try {
                PlaylistService service = GodHand.get(GodHand.K.PlaylistService);
                GodHand.<GroupItemListPanel>exec(GodHand.K.GroupItemListPanel, itemPanel -> {
                    List<PlayViewItem> playViewItemList = service.getChannelTitleList(groupTitleLabel.getTrimText());
                    if (CollectionUtil.isNotEmpty(playViewItemList)) {
                        itemPanel.setDataByMyPlayItem(playViewItemList);
                    }
                });
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        // 添加组件
        GodHand.register(GodHand.K.GroupListPanel, this);
        // 自定义
    }


    public class GroupItem extends JLabel {
        public GroupItem(String text) {
            super("  " + text);

            setOpaque(true);
        }

        public String getTrimText() {
            return super.getText().trim();
        }
    }

    /**
     * 右键菜单
     *
     * @return
     */
    @Override
    public BiConsumer<MouseEvent, GroupItem> getMouseConsumer() {
        return (mouseEvent, item) -> {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem("更改组名", Icons.Standard.FACE_HAPPY);
            popupMenu.add(menuItem);
            menuItem.addActionListener(e -> new ChangeGroupTitlePopup().open(item.getTrimText()));
            popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        };
    }
}
