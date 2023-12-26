package net.tv.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import net.tv.service.model.PlayViewItem;
import net.tv.util.HttpClient;
import net.tv.view.arm.ConsoleLog;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchTvUtil {

    private interface R {
        String URL_BASE = "http://tonkiang.us/";
        String URL_SEARCH = URL_BASE + "?page=%s&s=%s";
    }

    public static List<PlayViewItem> search(int pageIndex, String value) {
        List<PlayViewItem> viewItemList = new ArrayList<>();
        try {
            ConsoleLog.println("SearchTv 正在搜索...");
            Request request = new Request.Builder().url(String.format(R.URL_SEARCH, pageIndex, value)).build();
            Response response = HttpClient.getClient().newCall(request).execute();
            if (response.body() != null && response.code() == HttpStatus.HTTP_OK) {
                String html = IOUtils.toString(response.body().byteStream());
                Document document = Jsoup.parse(html, R.URL_BASE);
                Elements resultElements = document.getElementsByClass("result");
                resultElements.remove(0);
                for (Element element : resultElements) {
                    if (StrUtil.isBlank(element.text())) {
                        continue;
                    }
                    PlayViewItem playViewItem = getPlayViewItem(element);
                    if (playViewItem != null) viewItemList.add(playViewItem);
                }
                ConsoleLog.println("SearchTv 共查询到{}个节目.", viewItemList.size());
            }
        } catch (Exception ex) {
            ConsoleLog.println("SearchTv 搜索异常:{}", ex.getMessage());
        }
        return viewItemList;
    }

    private static PlayViewItem getPlayViewItem(Element element) {
        assert element != null;
        try {
            String channelTitle = element.child(0).child(0).child(0).text().trim();
            String mediaUrl = Objects.requireNonNull(element.getElementsByAttributeValue("class", "tables").last()).text().trim();
            return PlayViewItem.builder()
                    .channelTitle(channelTitle)
                    .mediaUrl(mediaUrl)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

}
