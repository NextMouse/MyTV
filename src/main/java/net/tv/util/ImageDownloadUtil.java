package net.tv.util;

import cn.hutool.http.HttpStatus;
import net.tv.view.arm.ConsoleLog;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.util.function.Consumer;

public class ImageDownloadUtil {


    public static void download(final String imgHttpUrl, final Consumer<ImageIcon> success, final Consumer<String> error) {
        AsyncUtil.exec(() -> {
            try {
                Request request = new Request.Builder().url(imgHttpUrl).build();
                Response response = HttpClient.getClient().newCall(request).execute();
                if (response.body() != null && response.code() == HttpStatus.HTTP_OK) {
                    ImageIcon imageIcon = new ImageIcon(response.body().bytes());
                    success.accept(imageIcon);
                } else {
                    error.accept(imgHttpUrl);
                }
            } catch (Exception e) {
                ConsoleLog.println(e);
                error.accept(imgHttpUrl);
            }
        });
    }

}
