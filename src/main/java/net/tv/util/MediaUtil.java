package net.tv.util;

import cn.hutool.http.HttpStatus;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MediaUtil {

    private static final OkHttpClient httpClient = HttpClient.getClient(10);

    /**
     * 基础check存活
     */
    public static boolean check(final String hlsUrl) {
        try {
            Response response = httpClient.newCall(new Request.Builder().url(hlsUrl).build()).execute();
            if (response.body() != null && response.code() == HttpStatus.HTTP_OK) {
                return true;
            }
        } catch (Exception ex) {
            // ignore
        }
        return false;
    }

}
