package net.tv.view.panel.root.right;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import net.tv.m3u.M3uParser;
import net.tv.m3u.Playlist;
import net.tv.service.model.PlayViewItem;
import net.tv.util.HttpClient;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.config.SystemConfig;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchSourceTvUtil {

    private final static ExecutorService executorService = Executors.newCachedThreadPool();

    public static List<Future<List<PlayViewItem>>> search(String searchValue) {
        final List<Future<List<PlayViewItem>>> futures = new ArrayList<>();
        if (StrUtil.isBlank(searchValue)) return futures;
        final String trimValue = searchValue.trim();
        if (getSourceList().isEmpty()) return futures;
        for (String sourceUrl : getSourceList()) {
            ConsoleLog.println("SourceTv 正在查询：{}", sourceUrl);
            Future<List<PlayViewItem>> future = executorService.submit(() -> getPlayViewItems(sourceUrl, trimValue));
            futures.add(future);
        }
        return futures;
    }

    private static List<PlayViewItem> getPlayViewItems(String sourceUrl, String searchValue) throws IOException {
        Request request = new Request.Builder().url(sourceUrl).build();
        Response response = HttpClient.getClient().newCall(request).execute();
        if (response.body() != null && response.code() == HttpStatus.HTTP_OK) {
            ConsoleLog.println("SourceTv 正在解析: {}", sourceUrl);
            try {
                Playlist playlist = new M3uParser().parseAndSearch(response.body().byteStream(), searchValue);
                return playlist.getItems().stream().map(PlayViewItem::getByPlayItem).toList();
            } catch (Exception ex) {
                ConsoleLog.println("SourceTv 解析失败: {}, error:{}", sourceUrl, ex.getMessage());
            }

        }
        return new ArrayList<>();
    }

    private static List<String> getSourceList() {
        SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
        return systemConfig.getTvSources();
    }

}
