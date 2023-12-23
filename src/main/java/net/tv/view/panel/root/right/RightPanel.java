package net.tv.view.panel.root.right;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import net.tv.service.model.PlayViewItem;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.*;
import net.tv.view.panel.root.center.VideoManagerToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RightPanel extends JPanel {

    interface R {
        Insets MARGIN = new Insets(0, 0, 0, 3);
        Dimension SCROLL_PANE_SIZE = new Dimension(180, -1);
    }

    private JTextField searchField;
    private final JButton nextSearchButton;

    public RightPanel() {
        setLayout(new BorderLayout());
        // 添加搜索结果
        add(getSearchResultPanel(), BorderLayout.CENTER);
        // 添加下一个按钮
        nextSearchButton = new JButton("更 多");
        nextSearchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextSearchButton.setVisible(false);
        nextSearchButton.addActionListener(e -> {
            try {
                int selectedIndex = searchResultListPanel.getSelectedIndex();
                searchPageIndex++;
                List<PlayViewItem> itemList = SearchTvUtil.search(searchPageIndex, searchValue);
                addResultList(itemList);
                // 重新指定选中位置
                if (selectedIndex != -1) {
                    searchResultListPanel.setSelectedIndex(selectedIndex);
                }
            } catch (Exception ex) {
                ConsoleLog.println(ex.getMessage());
            }
        });

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(nextSearchButton, BorderLayout.NORTH);
        searchPanel.add(getSearchPanel(), BorderLayout.SOUTH);
        // 添加下搜索框
        add(searchPanel, BorderLayout.SOUTH);
    }


    private int searchPageIndex = 1;
    private String searchValue;

    private JToolBar getSearchPanel() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(R.MARGIN);

        // 搜索框
        searchField = new JTextField();
        toolBar.add(searchField);

        SimpleButton button = new SimpleButton("搜索", Icons.Standard.SEARCH, (e, btn) -> {
            searchValue = searchField.getText();
            if (StrUtil.isNotBlank(searchValue)) {
                new Thread(() -> {
                    searchPageIndex = 1;
                    List<PlayViewItem> itemList = SearchTvUtil.search(searchPageIndex, searchValue);
                    setResultList(itemList);
                    nextSearchButton.setVisible(itemList.size() >= 30);
                }).start();
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
            GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, videoToolBar -> {
                GodHand.<JTextField>exec(GodHand.K.MediaLinkTextFiled, mediaLink -> mediaLink.setCaretPosition(0));
                // 搜索结果只播放
                GodHand.<MediaPlayerManager>exec(GodHand.K.MediaPlayerManager, playerManager -> {
                    String mediaUrl = selectedItem.getPlayViewItem().getMediaUrl();
                    if (StrUtil.isNotBlank(mediaUrl)) {
                        playerManager.load(mediaUrl).play();
                    }
                });
            });
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
        searchResultListPanel.setData(toResultPanelList(itemList));
    }

    private List<SearchResultPanel> toResultPanelList(List<PlayViewItem> itemList) {
        List<SearchResultPanel> addItemList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            addItemList.add(new SearchResultPanel(i + 1, itemList.get(i)));
        }
        return addItemList;
    }

    private void addResultList(List<PlayViewItem> itemList) {
        if (CollectionUtil.isNotEmpty(itemList)) {
            int initIndex = searchResultListPanel.getData().size() + 1;
            List<SearchResultPanel> addItemList = new ArrayList<>();
            for (int i = 0; i < itemList.size(); i++) {
                addItemList.add(new SearchResultPanel(initIndex + i, itemList.get(i)));
            }
            searchResultListPanel.setData(Stream.concat(searchResultListPanel.getData().stream(), addItemList.stream()).toList());
        } else {
            nextSearchButton.setVisible(false);
        }
    }

    public CustomizeComponent<SearchResultPanel> getCustomizeComponent() {
        return new CustomizeComponent<>() {

            @Override
            public void onMouseMoved(JList<SearchResultPanel> jList, SearchResultPanel comp) {
                comp.getNumLabel().setForeground(R.MOVED_FOREGROUND);
                comp.getChannelTitleLabel().setForeground(R.MOVED_FOREGROUND);
                comp.getMediaUrlLabel().setForeground(R.MOVED_FOREGROUND);
                comp.setBackground(R.MOVED_BACKGROUND);
                comp.setForeground(R.MOVED_FOREGROUND);
                comp.getResultPanel().setBackground(R.MOVED_BACKGROUND);
                comp.getResultPanel().setForeground(R.MOVED_FOREGROUND);
            }

            @Override
            public void onDefault(JList<SearchResultPanel> jList, SearchResultPanel comp) {
                comp.getNumLabel().setForeground(R.DEFAULT_FOREGROUND);
                comp.getChannelTitleLabel().setForeground(R.DEFAULT_FOREGROUND);
                comp.getMediaUrlLabel().setForeground(R.DEFAULT_FOREGROUND);
                comp.setForeground(R.DEFAULT_FOREGROUND);
                comp.getResultPanel().setForeground(R.DEFAULT_FOREGROUND);
                comp.setBackground(null);
                comp.getResultPanel().setBackground(null);
            }

            @Override
            public void onSelected(JList<SearchResultPanel> jList, SearchResultPanel comp) {
                comp.getNumLabel().setForeground(R.MOVED_FOREGROUND);
                comp.getChannelTitleLabel().setForeground(R.MOVED_FOREGROUND);
                comp.getMediaUrlLabel().setForeground(R.MOVED_FOREGROUND);
                comp.setForeground(R.MOVED_FOREGROUND);
                comp.getResultPanel().setForeground(R.MOVED_FOREGROUND);
                comp.setBackground(Color.PINK);
                comp.getResultPanel().setBackground(Color.PINK);
            }


        };
    }

}
