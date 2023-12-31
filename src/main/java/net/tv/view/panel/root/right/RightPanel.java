package net.tv.view.panel.root.right;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import net.tv.service.model.PlayViewItem;
import net.tv.util.AsyncUtil;
import net.tv.util.SearchSourceTvUtil;
import net.tv.util.SearchTvUtil;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.component.CustomizeComponent;
import net.tv.view.component.CustomizeList;
import net.tv.view.component.Icons;
import net.tv.view.component.SimpleButton;
import net.tv.view.component.impl.MediaPlayerProxy;
import net.tv.view.panel.root.center.VideoManagerToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class RightPanel extends JPanel {

    interface R {
        Insets MARGIN = new Insets(0, 0, 0, 3);
        Dimension SCROLL_PANE_SIZE = new Dimension(180, -1);
    }

    private JTextField searchField;
    private final JButton nextSearchButton;
    private final JButton moreButton = new JButton("更 多");
    ;
    private final CustomizeList<SearchResultPanel> searchResultListPanel1;
    private final CustomizeList<SearchResultPanel> searchResultListPanel2;

    public RightPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel panel1 = new JPanel(new BorderLayout());
        // 添加搜索结果
        searchResultListPanel1 = getSearchResultPanel();
        panel1.add(packResultPanel(searchResultListPanel1), BorderLayout.CENTER);
        // 添加下一个按钮
        nextSearchButton = getNextSearchButton();
        panel1.add(nextSearchButton, BorderLayout.SOUTH);

        JPanel panel2 = new JPanel(new BorderLayout());
        searchResultListPanel2 = getSearchResultPanel();
        panel2.add(packResultPanel(searchResultListPanel2), BorderLayout.CENTER);

        tabbedPane.addTab("SearchTV", panel1);
        tabbedPane.addTab("SourceTV", panel2);

        add(tabbedPane, BorderLayout.CENTER);
        add(getSearchPanel(), BorderLayout.SOUTH);
    }


    private JButton getNextSearchButton() {
        moreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        moreButton.setVisible(false);
        moreButton.addActionListener(e -> AsyncUtil.exec(() -> {
            try {
                int selectedIndex = searchResultListPanel1.getSelectedIndex();
                searchPageIndex++;
                List<PlayViewItem> playViewItems = SearchTvUtil.search(searchPageIndex, searchValue);
                addResultList(searchResultListPanel1, playViewItems);
                nextSearchButton.setVisible(playViewItems.size() >= 30);
                // 重新指定选中位置
                if (selectedIndex != -1) {
                    searchResultListPanel1.setSelectedIndex(selectedIndex);
                }
            } catch (Exception ex) {
                ConsoleLog.println(ex.getMessage());
            }
        }));
        return moreButton;
    }

    private int searchPageIndex = 1;
    private String searchValue;

    private JToolBar getSearchPanel() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(R.MARGIN);

        // 搜索框
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(110, searchField.getHeight()));
        toolBar.add(searchField);

        SimpleButton button = new SimpleButton("搜索", Icons.Standard.SEARCH, (e, btn) -> {
            this.moreButton.setVisible(false);
            searchValue = searchField.getText();
            if (StrUtil.isNotBlank(searchValue)) {
                final ExecutorService executor = Executors.newFixedThreadPool(2);
                executor.execute(() -> {
                    searchPageIndex = 1;
                    searchResultListPanel1.setData(new ArrayList<>());
                    List<PlayViewItem> playViewItems = SearchTvUtil.search(searchPageIndex, searchValue);
                    addResultList(searchResultListPanel1, playViewItems);
                    nextSearchButton.setVisible(playViewItems.size() >= 30);
                });
                executor.execute(() -> {
                    searchResultListPanel2.setData(new ArrayList<>());
                    List<Future<List<PlayViewItem>>> futures = SearchSourceTvUtil.search(searchValue);
                    for (Future<List<PlayViewItem>> future : futures) {
                        try {
                            List<PlayViewItem> playViewItems = future.get();
                            ConsoleLog.println("SourceTv 已搜索到：{}个", playViewItems.size());
                            addResultList(searchResultListPanel2, playViewItems);
                        } catch (Exception ex) {
                            // ignore
                            ConsoleLog.println("SourceTv 搜索异常：{}", ex.getMessage());
                        }
                    }
                });
                executor.shutdown();
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


    private JScrollPane packResultPanel(CustomizeList<SearchResultPanel> searchResultListPanel) {
        JScrollPane scrollPane = new JScrollPane(searchResultListPanel);
        scrollPane.setForeground(null);
        scrollPane.setBackground(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
        scrollPane.setPreferredSize(R.SCROLL_PANE_SIZE);
        return scrollPane;
    }

    private CustomizeList<SearchResultPanel> getSearchResultPanel() {
        CustomizeList<SearchResultPanel> customizeList = new CustomizeList<>(getCustomizeComponent(), selectedItem -> {
            if (selectedItem == null) return;
            GodHand.<VideoManagerToolBar>exec(GodHand.K.VideoManagerToolBar, videoToolBar -> {
                GodHand.<JTextField>exec(GodHand.K.MediaLinkTextFiled, mediaLink -> {
                    mediaLink.setText(selectedItem.getPlayViewItem().getMediaUrl());
                    mediaLink.setCaretPosition(0);
                });
                // 搜索结果只播放
                GodHand.<MediaPlayerProxy>exec(GodHand.K.MediaPlayerProxy, player -> {
                    player.play(selectedItem.getPlayViewItem().getMediaUrl());
                });
            });
        });
        customizeList.setMouseRightClickEvent((mouseEvent, item) -> {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem("复制");
            popupMenu.add(menuItem);
            menuItem.addActionListener(e -> {
                try {
                    PlayViewItem playViewItem = item.getPlayViewItem();
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(playViewItem.getPlayItem().toString()), null);
                    ConsoleLog.println("复制成功！");
                } catch (Exception ex) {
                    // ignore
                }
            });
            popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        });
        return customizeList;
    }

    private void addResultList(CustomizeList<SearchResultPanel> panel, List<PlayViewItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) return;
        int initIndex = panel.getData().size() + 1;
        List<SearchResultPanel> addItemList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            addItemList.add(new SearchResultPanel(initIndex + i, itemList.get(i)));
        }
        panel.setData(Stream.concat(panel.getData().stream(), addItemList.stream()).toList());
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
                comp.setBackground(R.SELECTED_BACKGROUND);
                comp.getResultPanel().setBackground(R.SELECTED_BACKGROUND);
            }

        };
    }

}
