package net.tv.view.panel.root.right;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import net.tv.service.model.PlayViewItem;
import net.tv.view.arm.ConsoleLog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchTvUtil {

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)//连接超时(单位:秒)
            .callTimeout(30, TimeUnit.SECONDS)//整个流程耗费的超时时间(单位:秒)--很少人使用
            .pingInterval(5, TimeUnit.SECONDS)//websocket轮训间隔(单位:秒)
            .readTimeout(60, TimeUnit.SECONDS)//读取超时(单位:秒)
            .writeTimeout(60, TimeUnit.SECONDS)//写入超时(单位:秒)
            .build();

    private interface R {
        String URL_BASE = "http://tonkiang.us/";
        String URL_SEARCH = URL_BASE + "?s=";
    }

    public static List<PlayViewItem> search(String value) {
        try {
            ConsoleLog.println("正在搜索...");
            Request request = new Request.Builder().url(R.URL_SEARCH + value).build();
            Response response = httpClient.newCall(request).execute();
            if (response.body() != null && response.code() == HttpStatus.HTTP_OK) {
                ConsoleLog.println("正在解析...");
                String html = IOUtils.toString(response.body().byteStream());
                Document document = Jsoup.parse(html, R.URL_BASE);
                Elements resultElements = document.getElementsByClass("result");
                resultElements.remove(0);
                List<PlayViewItem> viewItemList = new ArrayList<>();
                for (Element element : resultElements) {
                    if (StrUtil.isBlank(element.text())) {
                        continue;
                    }
                    PlayViewItem playViewItem = getPlayViewItem(element);
                    if (playViewItem != null) viewItemList.add(playViewItem);
                }
                ConsoleLog.println("共查询到{}个节目.", viewItemList.size());
                return viewItemList;
            }
        } catch (Exception ex) {
            ConsoleLog.println("搜索异常...");
        }
        return null;
    }

    private static PlayViewItem getPlayViewItem(Element element) {
        assert element != null;
        try {
            String channelTitle = element.child(0).child(0).child(0).text().trim();
            String mediaUrl = element.getElementsByAttributeValue("class", "tables").last().text().trim();
            return PlayViewItem.builder()
                    .channelTitle(channelTitle)
                    .mediaUrl(mediaUrl)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        SearchTvUtil.search("Qello");
    }

}
