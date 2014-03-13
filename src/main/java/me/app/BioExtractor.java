package me.app;

import me.utils.HtmlUtils;

import java.util.*;

/**
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 6:18 PM
 */
public class BioExtractor {
    public static final int BIO_LENGTH_MIN = 80;

    /**
     * 从任一个人Homepage html文档中抽取出个人简介
     * @param html
     * @return
     */
    public String extract(String html) {
        String text = HtmlUtils.getText(html);
        String[] chunks = sliceText(text);
        String bio = selectBio(chunks);
        return bio;
    }

    /**
     * 将html文本按文字段落大致分块
     * @param text
     * @return
     */
    public String[] sliceText(String text) {
        String sep = getSeperator(text);
        String[] trunks = text.split(sep);
        int len = trunks.length;
        for (int i = 0; i < len; i++) {
            trunks[i] = trunks[i].trim();
        }
        return trunks;
    }

    /**
     * 根据关键词在文本中出现的频率选取最有可能是个人简介的文本。
     * @param chunks
     * @return
     */
    private String selectBio(String[] chunks) {
        Map<Integer, List<String>> refinedchunkMap = new LinkedHashMap<Integer, List<String>>(); // key:chunk id, value: chunk tokens
        int len = chunks.length;
        for (int i = 0; i < len; i++) {
            // 分词
            List<String> words = HtmlUtils.token(chunks[i]);
            // 除用词过短的文本
            if (words.size() < BIO_LENGTH_MIN)
                continue;
            refinedchunkMap.put(i, words);
        }

        int targetIndex = -1;
        int maxScore = -1;
        for (Map.Entry<Integer, List<String>> entry : refinedchunkMap.entrySet()) {
            int score = 0;
            for (String str : entry.getValue()) {
                if (HtmlUtils.isBiokeyword(str)) score++;
            }
            if (score > maxScore) {
                maxScore = score;
                targetIndex = entry.getKey();
            }
        }
        System.out.println("Score: " + maxScore);
        return chunks[targetIndex];
    }

    /**
     * 使用文档连续若干个' '出现次数的期望来最为分隔符
     * @param text
     * @return
     */
    private String getSeperator(String text) {
        Map<Integer, Integer> crmap = new HashMap<Integer, Integer>();
        char[] chars = text.toCharArray();
        int len = chars.length;
        // count the occurance of the continuous x '\n'
        for (int i = 0; i < len;) {
            int counter = 0;
            while (i < len && chars[i] == '\n') {
                counter++;
                i++;
            }
            i++;
            if (crmap.containsKey(counter))
                crmap.put(counter, crmap.get(counter) + 1);
            else
                crmap.put(counter, 1);
        }
        if (crmap.containsKey(0))
            crmap.remove(0);
        float x = 0.01f;
        float y = 0.01f;
        for (Map.Entry<Integer, Integer> entry : crmap.entrySet()) {
            x += entry.getKey() * entry.getValue();
            y += entry.getValue();
        }
        int crnum = Math.round(x / y);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < crnum; i++) {
            sb.append("\n");
        }
        String sep = sb.toString();
        System.out.println("Seperator length: " + sep.length());
        return sep;
    }
}
