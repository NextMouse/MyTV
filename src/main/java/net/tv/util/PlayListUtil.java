package net.tv.util;

import cn.hutool.core.comparator.PinyinComparator;
import net.tv.service.model.PlayViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayListUtil {

    static final PinyinComparator pinyinComparator = new PinyinComparator();

    static final Pattern pattern = Pattern.compile("(\\d+|\\D+)");

    public static void sort(List<PlayViewItem> outList) {
        outList.sort((o1, o2) -> {
            if (o1.getGroupTitle().equalsIgnoreCase(o2.getGroupTitle())) {
                List<Tuple> o1TupleList = splitString(o1.getChannelTitle());
                List<Tuple> o2TupleList = splitString(o2.getChannelTitle());
                int minSize = Math.min(o1TupleList.size(), o2TupleList.size());
                for (int i = 0; i < minSize; i++) {
                    Tuple t1 = o1TupleList.get(i);
                    Tuple t2 = o2TupleList.get(i);
                    if (t1.isNumber && t2.isNumber && (t1.getNumber() != t2.getNumber())) {
                        return t1.getNumber() - t2.getNumber();
                    }
                    int comp = pinyinComparator.compare(t1.toString(), t2.toString());
                    if (comp != 0) return comp;
                }
                return o1.getChannelTitle().length() - o2.getChannelTitle().length();
            }
            return pinyinComparator.compare(o1.getGroupTitle(), o2.getGroupTitle());
        });
    }


    /**
     * 将给定的字符串分成数字和非数字部分，并将它们存储在元组中。
     * 数字部分的元组包含布尔值true和整数值，而非数字部分的元组包含布尔值false和字符串值。
     */
    private static List<Tuple> splitString(String input) {
        // 创建一个模式，用于匹配数字和非数字字符

        // 创建一个列表，用于存储元组
        List<Tuple> tuples = new ArrayList<>();

        // 使用正则表达式将字符串分成数字和字符串部分，并将它们存储在元组中
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String part = matcher.group();
            if (part.matches("\\d+")) { // 如果是数字
                tuples.add(new Tuple(true, Integer.parseInt(part)));
            } else { // 否则是字符串
                tuples.add(new Tuple(false, part));
            }
        }

        return tuples;
    }

    /**
     * 表示一个元组，其中包含一个布尔值和一个对象。
     * 如果布尔值为true，则表示对象是一个整数，否则表示对象是一个字符串。
     */
    private static class Tuple {
        private final boolean isNumber;
        private final Object value;

        public Tuple(boolean isNumber, Object value) {
            this.isNumber = isNumber;
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public int getNumber() {
            return isNumber ? (int) value : -1;
        }
    }

}
