package net.tv.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import net.tv.m3u.Constants;
import net.tv.m3u.M3uParser;
import net.tv.m3u.PlayItem;
import net.tv.m3u.Playlist;
import net.tv.service.model.PlayItemAvailable;
import net.tv.service.model.PlayViewItem;
import net.tv.service.orm.PlayViewItemDao;
import net.tv.view.arm.ConsoleLog;

import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistService {

    private final PlayViewItemDao playViewItemDao = new PlayViewItemDao();

    private final static M3uParser m3uParser = new M3uParser();

    public void readFile(File file) {
        try {
            ConsoleLog.println("解析：{}", file.getAbsolutePath());
            Playlist playlist = m3uParser.parse(IoUtil.toStream(file));
            ConsoleLog.println("解析完成：{}个节目", CollectionUtil.size(playlist.getItems()));
            if (CollectionUtil.isEmpty(playlist.getItems())) return;
            playViewItemDao.batchInsert(getPlayViewItemList(playlist.getItems()));
        } catch (Exception e) {
            ConsoleLog.println(e);
        }
    }

    private List<PlayViewItem> getPlayViewItemList(List<PlayItem> playItemList) {
        return playItemList.stream()
                .map(PlayViewItem::getByPlayItem)
                .collect(Collectors.toList());
    }

    public List<String> getByGroupTitle() {
        try {
            return playViewItemDao.queryGroupTitleList();
        } catch (SQLException e) {
            ConsoleLog.println(e);
            return null;
        }
    }

    public List<PlayViewItem> getChannelTitleList(String groupTitle) {
        try {
            return playViewItemDao.queryByGroupTitle(groupTitle);
        } catch (SQLException e) {
            ConsoleLog.println(e);
            return null;
        }
    }

    public void addOrUpdate(PlayViewItem playViewItem) {
        try {
            if (StrUtil.isBlank(playViewItem.getId())) {
                playViewItem.setId(IdUtil.simpleUUID());
                playViewItemDao.insert(playViewItem);
            } else {
                playViewItemDao.update(playViewItem);
            }
        } catch (SQLException e) {
            ConsoleLog.println(e);
        }
    }

    public void delete(PlayViewItem playViewItem) {
        try {
            if (StrUtil.isNotBlank(playViewItem.getId())) playViewItemDao.delete(playViewItem);
        } catch (SQLException e) {
            ConsoleLog.println(e);
        }
    }

    public int updateGroupTitle(String oldGroupTitle, String newGroupTitle) {
        try {
            return playViewItemDao.updateGroupTitle(oldGroupTitle, newGroupTitle);
        } catch (SQLException e) {
            ConsoleLog.println(e);
        }
        return -1;
    }

    public void saveToFile(File file) {
        if (file == null) {
            ConsoleLog.println("请选择文件");
            return;
        }
        try (FileWriter fos = new FileWriter(file)) {
            List<PlayViewItem> playViewItemList = playViewItemDao.queryAll();
            fos.write(Constants.EXTM3U);
            int i = 1;
            for (PlayViewItem playViewItem : playViewItemList) {
                fos.write(System.lineSeparator());
                fos.write(playViewItem.getPlayItem()
                        .toString());
                i++;
                if (i > 100) {
                    fos.flush();
                    i = 1;
                }
            }
            fos.flush();
        } catch (Exception e) {
            ConsoleLog.println("保存文件错误：{}", file.getAbsolutePath());
            ConsoleLog.println(e);
        }
        ConsoleLog.println("保存成功：{}", file.getAbsolutePath());
    }

    public void distinct() {
        try {
            List<PlayViewItem> playViewItemList = playViewItemDao.queryAll();
            playViewItemDao.truncate();
            List<PlayViewItem> newList = playViewItemList.stream().distinct().collect(Collectors.toList());
            playViewItemDao.batchInsert(newList);
            ConsoleLog.println("去重成功，当前：{}条", newList.size());
        } catch (SQLException e) {
            ConsoleLog.println(e);
        }

    }
}
