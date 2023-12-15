package net.tv.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import net.tv.view.arm.ConsoleLog;
import net.tv.view.arm.GodHand;
import net.tv.view.config.SystemConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ImageDownloadUtil {
    private static final OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)//连接超时(单位:秒)
            .callTimeout(120, TimeUnit.SECONDS)//整个流程耗费的超时时间(单位:秒)--很少人使用
            .pingInterval(5, TimeUnit.SECONDS)//websocket轮训间隔(单位:秒)
            .readTimeout(60, TimeUnit.SECONDS)//读取超时(单位:秒)
            .writeTimeout(60, TimeUnit.SECONDS)//写入超时(单位:秒)
            ;

    public static OkHttpClient getClient() {
        SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
        String hostname = systemConfig.getTvgLogo().getHostname();
        String port = systemConfig.getTvgLogo().getPort();
        if (StrUtil.isNotBlank(hostname) && StrUtil.isNotBlank(port)) {
            ConsoleLog.println("当前代理配置：{}:{}", hostname, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, Integer.parseInt(port)));
            builder.proxy(proxy);
        }
        return builder.build();
    }

    public static void download(final String imgHttpUrl, final Consumer<ImageIcon> success, final Consumer<String> error) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder().url(imgHttpUrl).build();
                Response response = getClient().newCall(request).execute();
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
        }).start();
    }

}
