package net.tv.m3u;

import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Playlist {

    /**
     * playlist items
     */
    private List<PlayItem> items;

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(Constants.EXTM3U);
        if (CollectionUtil.isEmpty(items)) return strBuilder.toString();
        items.forEach(item -> strBuilder.append(System.lineSeparator()).append(item.toString()));
        return strBuilder.toString();
    }

}
