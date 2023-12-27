package net.tv.service.model;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.tv.m3u.ExtInfInfo;
import net.tv.m3u.PlayItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.tv.m3u.ExtInfInfo.AttrKey.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayViewItem implements Serializable {

    private String id;

    private Integer duration;

    private String groupTitle;

    private String channelTitle;

    private String tvgId;

    private String tvgName;

    private String tvgLogo;

    private String aspectRatio;

    private String mediaUrl;

    public static PlayViewItem getByPlayItem(PlayItem playItem) {
        return PlayViewItem
                .builder()
                .id(IdUtil.simpleUUID())
                .duration(playItem.getExtInf()
                        .getDuration())
                .groupTitle(playItem.getAttrValue(GroupTitle))
                .channelTitle(playItem.getChannelTitle())
                .tvgId(playItem.getAttrValue(TvgId))
                .tvgName(playItem.getAttrValue(TvgName))
                .tvgLogo(playItem.getAttrValue(TvgLogo))
                .aspectRatio(playItem.getAttrValue(AspectRatio))
                .mediaUrl(playItem.getMediaList()
                        .get(0))
                .build();
    }

    public PlayItem getPlayItem() {
        PlayItem playItem = new PlayItem();

        ExtInfInfo extInfInfo = new ExtInfInfo();
        extInfInfo.setDuration(duration);
        extInfInfo.setChannelTitle(channelTitle);

        Map<ExtInfInfo.AttrKey, String> attrMap = new HashMap<>();
        if (StrUtil.isNotBlank(groupTitle)) attrMap.put(GroupTitle, groupTitle);
        if (StrUtil.isNotBlank(tvgId)) attrMap.put(TvgId, tvgId);
        if (StrUtil.isNotBlank(tvgName)) attrMap.put(TvgName, tvgName);
        if (StrUtil.isNotBlank(tvgLogo)) attrMap.put(TvgLogo, tvgLogo);
        if (StrUtil.isNotBlank(aspectRatio)) attrMap.put(AspectRatio, aspectRatio);
        extInfInfo.setAttributes(attrMap);

        playItem.setExtInf(extInfInfo);

        playItem.setMediaList(List.of(mediaUrl));
        return playItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayViewItem that = (PlayViewItem) o;

        if (!Objects.equals(groupTitle, that.groupTitle)) return false;
        if (!Objects.equals(channelTitle, that.channelTitle)) return false;
        return Objects.equals(mediaUrl, that.mediaUrl);
    }

    @Override
    public int hashCode() {
        int result = groupTitle != null ? groupTitle.hashCode() : 0;
        result = 31 * result + (channelTitle != null ? channelTitle.hashCode() : 0);
        result = 31 * result + (mediaUrl != null ? mediaUrl.hashCode() : 0);
        return result;
    }
}
