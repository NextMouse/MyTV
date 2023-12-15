package net.tv.m3u;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExtInfInfo {

    public enum AttrKey {

        /**
         * TV guide time shifting
         */
        TvgShift("tvg-shift"),

        /**
         * TV guide identifier
         */
        TvgId("tvg-id"),

        /**
         * TV guide identifier
         */
        TvgName("tvg-name"),

        /**
         * channel's logo (url)
         */
        TvgLogo("tvg-logo"),

        /**
         * Audio track definition of this channel, if it's supported by stream.
         * Write language codes in ISO 639-2 standard, you may use several codes separated by comma (e.g.: "eng, rus, deu").
         * The first item in the list will be defined as default.
         */
        AudioTrack("audio-track"),

        /**
         * defines aspec ratio (may be not available for some TV models).
         * Available values: 16:9, 3:2, 4:3, 1,85:1, 2,39:1
         */
        AspectRatio("aspect-ratio"), Ratio("ratio"),
        /**
         *
         */
        GroupTitle("group-title");
        private String name;

        AttrKey(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * In case of live TV links the duration has always to be 0 or -1
     */
    private Integer duration;

    /**
     * allows to telle the app tyepe of content described. Available values: stream, video, playlist.
     * In most cases the app defines content type automatically and there is no need to use this parameter.
     */
    private String contentType;

    /**
     * 属性列表
     */
    private Map<AttrKey, String> attributes;

    /**
     * 原始属性字符串
     */
    private String rawAttrs;

    /**
     * 名称
     */
    private String channelTitle;

    @Override
    public String toString() {
        return "ExtInfInfo{" + "duration=" + duration + ", attributes=" + attributes + ", channelTitle='" + channelTitle + '\'' + ", contentType='" + contentType + '\'' + '}';
    }
}
