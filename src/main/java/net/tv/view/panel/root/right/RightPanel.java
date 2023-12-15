package net.tv.view.panel.root.right;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import net.tv.service.model.PlayViewItem;
import net.tv.view.arm.GodHand;
import net.tv.view.component.CustomizeComponent;
import net.tv.view.component.CustomizeList;
import net.tv.view.component.Icons;
import net.tv.view.component.SimpleButton;
import net.tv.view.panel.root.center.VideoManagerToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class RightPanel extends JPanel {

    interface R {
        Insets MARGIN = new Insets(0, 0, 0, 3);
        Dimension SCROLL_PANE_SIZE = new Dimension(180, -1);
    }

    public RightPanel() {
        setLayout(new BorderLayout());
        // 添加搜索结果
        add(getSearchResultPanel(), BorderLayout.CENTER);
        // 添加下搜索框
        add(getSearchPanel(), BorderLayout.SOUTH);
    }

    private JTextField searchField;

    private JToolBar getSearchPanel() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(R.MARGIN);

        // 搜索框
        searchField = new JTextField();
        toolBar.add(searchField);

        SimpleButton button = new SimpleButton("搜索", Icons.Standard.SEARCH, (e, btn) -> {
            String searchValue = searchField.getText();
            if (StrUtil.isNotBlank(searchValue)) {
                List<PlayViewItem> itemList = SearchTvUtil.search(searchValue);
                setResultList(itemList);
            }
        });

        // 搜索按钮
        toolBar.add(button);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });

        return toolBar;
    }

    private CustomizeList<SearchResultPanel> searchResultListPanel;

    private JScrollPane getSearchResultPanel() {
        searchResultListPanel = new CustomizeList<>(getCustomizeComponent(), selectedItem -> {
            if (selectedItem == null) return;
            GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, videoToolBar -> videoToolBar.setPlayViewItem(selectedItem.getPlayViewItem()));
        });
        JScrollPane scrollPane = new JScrollPane(searchResultListPanel);
        scrollPane.setForeground(null);
        scrollPane.setBackground(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
        scrollPane.setPreferredSize(R.SCROLL_PANE_SIZE);
        return scrollPane;
    }

    private void setResultList(List<PlayViewItem> itemList) {
        if (CollectionUtil.isNotEmpty(itemList)) {
            searchResultListPanel.setData(itemList.stream().map(SearchResultPanel::new).collect(Collectors.toList()));
        }
    }

    public CustomizeComponent<SearchResultPanel> getCustomizeComponent() {
        return new CustomizeComponent<>() {
            @Override
            public void onMouseMoved(JList<SearchResultPanel> jList, SearchResultPanel comp) {
                comp.getChannelTitleLabel().setForeground(R.MOVED_FOREGROUND);
                comp.getMediaUrlLabel().setForeground(R.MOVED_FOREGROUND);
                comp.setBackground(R.MOVED_BACKGROUND);
                comp.setForeground(R.MOVED_FOREGROUND);
            }

            @Override
            public void onDefault(JList<SearchResultPanel> jList, SearchResultPanel comp) {
                comp.getChannelTitleLabel().setForeground(R.DEFAULT_FOREGROUND);
                comp.getMediaUrlLabel().setForeground(R.DEFAULT_FOREGROUND);
                comp.setForeground(R.DEFAULT_FOREGROUND);
                comp.setBackground(null);
            }
        };
    }

}
