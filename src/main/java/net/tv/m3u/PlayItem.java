package net.tv.m3u;

import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PlayItem {

    /**
     * track information: runtime in seconds and display title of the following resource
     */
    private ExtInfInfo extInf;

    /**
     * begin named grouping
     */
    private String extGrp;

    /**
     * album information, title in particular
     */
    private String extAlb;

    /**
     * 播放列表
     */
    private List<String> mediaList;

    @Override
    public String toString() {
        // 输出 ExtInf
        StringBuilder strBuild = new StringBuilder();
        // #EXTINF
        strBuild.append(Constants.EXTINF)
                .append(extInf.getDuration())
                .append(",");
        // Attr
        Map<ExtInfInfo.AttrKey, String> attrMap = extInf.getAttributes();
        if (MapUtil.isNotEmpty(attrMap)) {
            attrMap.forEach(((attrKey, value) -> {
                strBuild.append(" ")
                        .append(attrKey.getName())
                        .append("=\"")
                        .append(value)
                        .append("\"");
            }));
            strBuild.append(",");
        }
        strBuild.append(extInf.getChannelTitle());
        mediaList.forEach(mediaUri -> {
            strBuild.append(System.lineSeparator())
                    .append(mediaUri);
        });
        return strBuild.toString();
    }

    public String getChannelTitle() {
        return extInf.getChannelTitle();
    }

    public String getAttrValue(ExtInfInfo.AttrKey attrKey) {
        return extInf.getAttributes()
                     .get(attrKey);
    }

    public String setAttrValue(ExtInfInfo.AttrKey attrKey, String value) {
        return extInf.getAttributes()
                     .put(attrKey, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayItem playItem = (PlayItem) o;
        return Objects.equals(mediaList, playItem.mediaList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaList);
    }


}
