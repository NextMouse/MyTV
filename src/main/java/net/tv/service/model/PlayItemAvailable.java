package net.tv.service.model;

import cn.hutool.crypto.digest.MD5;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class PlayItemAvailable {


    private String id;

    private String mediaUrl;

    private PlayItemAvailable() {

    }

    public PlayItemAvailable(String mediaUrl) {
        this.id = MD5.create()
                     .digestHex(mediaUrl, StandardCharsets.UTF_8);
        this.mediaUrl = mediaUrl;
    }

    public PlayItemAvailable(String id, String mediaUrl) {
        this.id = id;
        this.mediaUrl = mediaUrl;
    }

}
