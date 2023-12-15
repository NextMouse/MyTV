package net.tv.view.config;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.tv.view.arm.ConsoleLog;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class SystemConfig {

    public static final String SYSTEM_CONFIG_URL = R.SYSTEM_CONFIG_PATH + "/my-tv.properties";
    public static final String TEMP_DB_FILE_PATH = R.SYSTEM_CONFIG_PATH + "/temp.db";
    public static final String DB_FILE_PATH = R.SYSTEM_CONFIG_PATH + "/my-tv.db";

    private ThemeConfig theme;

    private ProxyConfig tvgLogo;

    private String openDirPath;

    public static SystemConfig getDefault() {
        return SystemConfig.builder()
                .theme(ThemeConfig.getDefault())
                .tvgLogo(ProxyConfig.getDefault())
                .openDirPath(R.USER_HOME_PATH)
                .build();
    }

    interface R {
        String SYSTEM_CONFIG_PATH = System.getProperty("user.dir");
        String USER_HOME_PATH = FileSystemView.getFileSystemView()
                .getHomeDirectory()
                .getAbsolutePath();
        String OPEN_DIR_PATH = "m3u.open-path";
        String THEME_BASE_PREFIX = "theme.";
        String THEME_SYSTEM = THEME_BASE_PREFIX + "system";
        String TVG_LOGO_PROXY_BASE_PREFIX = "tvg-logo.proxy.";
        String TVG_LOGO_PROXY_HOSTNAME = TVG_LOGO_PROXY_BASE_PREFIX + "hostname";
        String TVG_LOGO_PROXY_PORT = TVG_LOGO_PROXY_BASE_PREFIX + "port";

    }

    public static SystemConfig load() {
        return FileUtil.load(SYSTEM_CONFIG_URL, StandardCharsets.UTF_8, reader -> {
            SystemConfig config = SystemConfig.getDefault();
            String line;
            while ((line = reader.readLine()) != null) {
                if (StrUtil.isBlank(line)) continue;
                if (!line.contains("=")) continue;
                if (line.startsWith("#") || line.startsWith("=")) continue;
                line = line.trim();
                final String[] tempKV = line.split("=");
                final String key = tempKV[0].trim();
                final String value = tempKV[1].trim();
                if (key.equalsIgnoreCase(R.OPEN_DIR_PATH)) {
                    config.setOpenDirPath(value);
                } else if (key.equalsIgnoreCase(R.THEME_SYSTEM)) {
                    config.theme.setSystem(ThemeConfig.Key.getOrDefault(value));
                } else if (key.equalsIgnoreCase(R.TVG_LOGO_PROXY_HOSTNAME)) {
                    config.tvgLogo.setHostname(value);
                } else if (key.equalsIgnoreCase(R.TVG_LOGO_PROXY_PORT)) {
                    config.tvgLogo.setPort(value);
                }
            }
            return config;
        });
    }

    public void save() {
        final String separator = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("# ")
                .append(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN))
                .append(separator);
        sb.append(R.OPEN_DIR_PATH)
                .append("=")
                .append(openDirPath)
                .append(separator);
        final String tvgLogoProxyHostName = tvgLogo.getHostname();
        final String tvgLogoProxyPort = String.valueOf(tvgLogo.getPort());
        if (StrUtil.isNotBlank(tvgLogoProxyHostName) && StrUtil.isNotBlank(tvgLogoProxyPort)) {
            sb.append(R.TVG_LOGO_PROXY_HOSTNAME)
                    .append("=")
                    .append(tvgLogo.getHostname())
                    .append(separator);
            sb.append(R.TVG_LOGO_PROXY_PORT)
                    .append("=")
                    .append(tvgLogo.getPort())
                    .append(separator);
        }
        sb.append(R.THEME_SYSTEM)
                .append("=")
                .append(theme.getSystem()
                        .toString())
                .append(separator);
        FileUtil.writeString(StrUtil.utf8Str(sb.toString()), new File(SYSTEM_CONFIG_URL), StandardCharsets.UTF_8);
        ConsoleLog.println("保存配置 ==> {}", SYSTEM_CONFIG_URL);
    }

}
