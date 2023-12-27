package net.tv.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.util.function.Consumer;

public class ImageDownloadUtil {

    public static void download(final String imgHttpUrl, final Runnable loading, final Consumer<ImageIcon> success, final Consumer<String> error) {
        if (StrUtil.isBlank(imgHttpUrl)) {
            error.accept(imgHttpUrl);
            return;
        }
        if (loading != null) loading.run();
        AsyncUtil.exec(() -> {
            Response response = null;
            try {
                Request request = new Request.Builder().url(imgHttpUrl).build();
                response = HttpClient.getProxyClient().newCall(request).execute();
                if (response.body() != null && response.code() == HttpStatus.HTTP_OK) {
                    ImageIcon imageIcon = new ImageIcon(response.body().bytes());
                    success.accept(imageIcon);
                } else {
                    error.accept(imgHttpUrl);
                }
            } catch (Exception e) {
                error.accept(imgHttpUrl);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        });
    }

}
