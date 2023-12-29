package net.tv.m3u;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
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
     */
    public Playlist parse(InputStream inputStream) {
        return parseAndSearch(inputStream, null);
    }

    public PlayItem parseOne(List<String> lines) throws M3uFileFormatException {
        List<String> notBlankLines = lines.stream().filter(e -> !e.isBlank()).toList();
        if (notBlankLines.isEmpty()) return null;
        String line1 = notBlankLines.get(0);
        ExtInfInfo extInfInfo = parseExtInf(line1);
        PlayItem playItem = PlayItem.builder().extInf(extInfInfo).mediaList(new ArrayList<>()).build();
        Map<AttrKey, String> attributes = parseExtInfoAttrs(extInfInfo.getRawAttrs());
        extInfInfo.setAttributes(attributes);
        if (notBlankLines.size() > 1) {
            for (int i = 1; i < notBlankLines.size(); i++) {
                String line2 = notBlankLines.get(i);
                if (MEDIA_REGEX.matcher(line2).matches()) {
                    playItem.getMediaList().add(line2);
                }
            }
        }
        return playItem;
    }

    public Playlist parseAndSearch(InputStream inputStream, String searchValue) {
        assert inputStream != null;
        Playlist playlist = new Playlist();
        List<PlayItem> playItemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            int lineNum = 1;
            LineInfo lineInfo = getNotBlankLine(reader, lineNum);
            String line = lineInfo.line;
            if (!line.startsWith(Constants.EXTM3U)) {
                throw new M3uFileFormatException(lineInfo.num, line, "文件必须以 " + Constants.EXTM3U + " 开头");
            }
            PlayItem playItem = null;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (StrUtil.isBlank(line)) continue;
                line = line.trim();
                try {
                    if (line.startsWith(Constants.EXTINF)) {
                        ExtInfInfo extInfInfo = parseExtInf(line);
                        if (searchValue != null && !StrUtil.containsIgnoreCase(extInfInfo.getChannelTitle(), searchValue)) {
                            // 需要查找但没有找到，则忽略该条目
                            continue;
                        }
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
                    }
                } catch (Exception ex) {
                    playItem = null;
                }
            }
        } catch (IOException | M3uFileFormatException e) {
            throw new RuntimeException(e);
        }
        playlist.setItems(playItemList);
        return playlist;
    }

    @AllArgsConstructor
    public static class LineInfo {
        public String line;
        public int num;
    }

    private LineInfo getNotBlankLine(BufferedReader reader, int initNum) throws IOException {
        String line = reader.readLine();
        initNum++;
        while (StrUtil.isBlank(line)) {
            line = reader.readLine();
            initNum++;
        }
        return new LineInfo(line, initNum);
    }

    private static final Pattern MEDIA_REGEX = Pattern.compile("[^#]+://\\S*", Pattern.CASE_INSENSITIVE);

    private static final String ATTR_REGEX = "\\s*,?\\s*%s\\s*=\\s*\"(?<attrValue>.*?)\".*";
    private static final Map<AttrKey, Pattern> ExtInf_ATTR_REGEX = new HashMap<>();

    static {
        AttrKey[] attrKeys = AttrKey.values();
        for (AttrKey attrKey : attrKeys) {
            ExtInf_ATTR_REGEX.put(attrKey, Pattern.compile(String.format(ATTR_REGEX, attrKey.getName()), Pattern.CASE_INSENSITIVE));
        }
    }

    private Map<AttrKey, String> parseExtInfoAttrs(String rawAttrs) {
        Map<AttrKey, String> attrMap = new HashMap<>();
        for (Map.Entry<AttrKey, Pattern> attrKeyAndRegex : ExtInf_ATTR_REGEX.entrySet()) {
            Matcher matcher = attrKeyAndRegex.getValue().matcher(rawAttrs);
            if (!matcher.find()) continue;
            attrMap.put(attrKeyAndRegex.getKey(), matcher.group("attrValue"));
        }
        return attrMap;
    }

    private static final Pattern ExtInf_REGEX = Pattern.compile("#EXTINF:(?<duration>[0-9|-]+)\\s*,?\\s*(?<attributes>.*),(?<channelTitle>.*)", Pattern.CASE_INSENSITIVE);

    /**
     * 解析行到 Constants.ExtInf
     */
    public ExtInfInfo parseExtInf(String line) throws M3uFileFormatException {
        Matcher matcher = ExtInf_REGEX.matcher(line);
        if (!matcher.find()) {
            throw new M3uFileFormatException("不符合#EXIINF规定格式");
        }
        return ExtInfInfo.builder().duration(Integer.valueOf(matcher.group("duration"))).rawAttrs(matcher.group("attributes")).channelTitle(matcher.group("channelTitle")).build();
    }

}
