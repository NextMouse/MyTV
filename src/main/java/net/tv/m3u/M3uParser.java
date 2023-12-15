package net.tv.m3u;

import cn.hutool.core.util.StrUtil;
import net.tv.m3u.ExtInfInfo.AttrKey;
import net.tv.m3u.err.M3uFileFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3uParser {

    /**
     * 解析M3U文件
     *
     * @param inputStream
     * @return
     */
    public Playlist parse(InputStream inputStream) {
        assert inputStream != null;
        Playlist playlist = new Playlist();
        List<PlayItem> playItemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            int lineNum = 1;
            // 判断首行
            String line = reader.readLine();
            if (!Constants.EXTM3U.equals(line)) {
                throw new M3uFileFormatException(lineNum, line, "文件必须以" + Constants.EXTM3U + "开头");
            }
            PlayItem playItem = null;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (StrUtil.isBlank(line)) continue;
                line = line.trim();
                if (line.startsWith(Constants.EXTINF)) {
                    ExtInfInfo extInfInfo = parseExtInf(line);
                    playItem = PlayItem.builder().extInf(extInfInfo).mediaList(new ArrayList<>()).build();
                    playItemList.add(playItem);
                    // 解析参数
                    Map<AttrKey, String> attributes = parseExtInfoAttrs(extInfInfo.getRawAttrs());
                    extInfInfo.setAttributes(attributes);
                } else if (MEDIA_REGEX.matcher(line).matches() && playItem != null) {
                    playItem.getMediaList().add(line);
                } else if (line.startsWith(Constants.EXTBG) && playItem != null) {
                    playItem.setExtGrp(line.substring(Constants.EXTBG.length()));
                } else if (line.startsWith(Constants.EXTLAB) && playItem != null) {
                    playItem.setExtAlb(line.substring(Constants.EXTLAB.length()));
                } else if (line.startsWith(Constants.COMMENT)) {
                    // ignore
                } else {
                    throw new M3uFileFormatException(lineNum, line, "未知格式无法解析");
                }
            }
        } catch (IOException | M3uFileFormatException e) {
            throw new RuntimeException(e);
        }
        playlist.setItems(playItemList);
        return playlist;
    }

    private static final Pattern MEDIA_REGEX = Pattern.compile("[^#]+://\\S*", Pattern.CASE_INSENSITIVE);

    private static final String ATTR_REGEX = "\\s*,?\\s*%s\\s*=\\s*\"(?<attrValue>.*?)\".*";
    private static final Map<AttrKey, Pattern> EXTINF_ATTR_REGEX = new HashMap<>();

    {
        AttrKey[] attrKeys = AttrKey.values();
        for (AttrKey attrKey : attrKeys) {
            EXTINF_ATTR_REGEX.put(attrKey, Pattern.compile(String.format(ATTR_REGEX, attrKey.getName(), Pattern.CASE_INSENSITIVE)));
        }
    }

    private Map<AttrKey, String> parseExtInfoAttrs(String rawAttrs) {
        Map<AttrKey, String> attrMap = new HashMap<>();
        for (Map.Entry<AttrKey, Pattern> attrKeyAndRegex : EXTINF_ATTR_REGEX.entrySet()) {
            Matcher matcher = attrKeyAndRegex.getValue().matcher(rawAttrs);
            if (!matcher.find()) continue;
            attrMap.put(attrKeyAndRegex.getKey(), matcher.group("attrValue"));
        }
        return attrMap;
    }

    private static final Pattern EXTINF_REGEX = Pattern.compile("#EXTINF:(?<duration>[0-9|-]+)\\s*,?\\s*(?<attributes>.*),(?<channelTitle>.*)", Pattern.CASE_INSENSITIVE);

    /**
     * 解析行到 Constants.EXTINF
     *
     * @param line
     * @return
     */
    public ExtInfInfo parseExtInf(String line) throws M3uFileFormatException {
        Matcher matcher = EXTINF_REGEX.matcher(line);
        if (!matcher.find()) {
            throw new M3uFileFormatException("不符合#EXIINF规定格式");
        }
        return ExtInfInfo.builder().duration(Integer.valueOf(matcher.group("duration"))).rawAttrs(matcher.group("attributes")).channelTitle(matcher.group("channelTitle")).build();
    }

}
