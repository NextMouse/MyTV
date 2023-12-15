package net.tv.view.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProxyConfig {

    private String hostname;

    private String port;

    public static ProxyConfig getDefault() {
        return ProxyConfig.builder()
                .build();
    }

}
