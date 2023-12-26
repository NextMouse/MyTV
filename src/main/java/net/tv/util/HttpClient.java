package net.tv.util;

import cn.hutool.core.util.StrUtil;
import net.tv.view.arm.GodHand;
import net.tv.view.config.SystemConfig;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    public static OkHttpClient getClient() {
        final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)//连接超时(单位:秒)
                .callTimeout(120, TimeUnit.SECONDS)//整个流程耗费的超时时间(单位:秒)--很少人使用
                .pingInterval(5, TimeUnit.SECONDS)//websocket轮训间隔(单位:秒)
                .readTimeout(60, TimeUnit.SECONDS)//读取超时(单位:秒)
                .writeTimeout(60, TimeUnit.SECONDS)//写入超时(单位:秒)
                ;
        SystemConfig systemConfig = GodHand.get(GodHand.K.SystemConfig);
        String hostname = systemConfig.getTvgLogo().getHostname();
        String port = systemConfig.getTvgLogo().getPort();
        if (StrUtil.isNotBlank(hostname) && StrUtil.isNotBlank(port)) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, Integer.parseInt(port)));
            okHttpClientBuilder.proxy(proxy);
        }
        return okHttpClientBuilder.build();
    }


}
