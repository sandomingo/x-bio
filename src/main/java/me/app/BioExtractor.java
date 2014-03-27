package me.app;

import me.utils.HtmlUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 6:18 PM
 */
public class BioExtractor {
    public static final int BIO_LENGTH_MIN = 30;
    public static final int BIO_LENGTH_MAX = 800;
    private static final float SCORE = 2;
    public static final String NO_BIO_FOUND = "No bio found.";
    private static BioExtractor instance;

    public static BioExtractor getInstance() {
        if (instance == null) {
            instance = new BioExtractor();
        }
        return instance;
    }
    /**
     * 从任一个人Homepage html文档中抽取出个人简介
     * @param html
     * @return
     */
    public String extract(String html) {
        String text = HtmlUtils.getText(html);
        String[] chunks = sliceText(text);
        String bio = selectBio(chunks);
        // prevent wrong sentence segmentation
        bio = bio.replace("Ph. D", "Ph.D");
        return bio;
    }

    /**
     * 将html文本按文字段落大致分块
     * @param text
     * @return
     */
    public String[] sliceText(String text) {
        String sep = getSeparator(text);
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
        Map<Integer, List<String>> refinedchunkMap = new TreeMap<Integer, List<String>>(); // key:chunk id, value: chunk tokens
        int len = chunks.length;
        for (int i = 0; i < len; i++) {
            // 取出不是已句号结尾的段落。从而获得完整的句子。
            String[] ps = chunks[i].split("\\n");
            StringBuilder sb = new StringBuilder();
            for (String p : ps) {
                if (p.trim().endsWith(".")) {
                    sb.append(p + "\n");
                }
            }
            chunks[i] = sb.toString();

            // 分词
            List<String> words = HtmlUtils.token(chunks[i]);
            // 去除用词过短的文本
            if (words.size() < BIO_LENGTH_MIN || words.size() > BIO_LENGTH_MAX)
                continue;
            refinedchunkMap.put(i, words);
        }

        StringBuilder bioBuilder = new StringBuilder();
        for (Map.Entry<Integer, List<String>> entry : refinedchunkMap.entrySet()) {
            double score = 0;
            for (String word : entry.getValue()) {
                if (HtmlUtils.isBiokeyword(word)) score+=1;
            }
            score = score * 10.0 / entry.getValue().size();
            if (score > SCORE) {
                bioBuilder.append(chunks[entry.getKey()] + "\n");
            }
        }
        String bioStr = bioBuilder.toString();

        // remove extra space
        bioStr = HtmlUtils.removeExtraSpace(bioStr);
        if (bioStr.trim().isEmpty())
            return NO_BIO_FOUND;
        else
            return bioStr;
    }

    /**
     * 使用文档连续若干个'\n'出现次数的期望来最为分隔符
     * @param text
     * @return
     */
    private String getSeparator(String text) {
        Map<Integer, Integer> statsMap = new HashMap<Integer, Integer>();// number of '\n' <-> occurrence
        char[] chars = text.toCharArray();
        int len = chars.length;
        // count the occurrence of the continuous x '\n'
        for (int i = 0; i < len;) {
            int counter = 0;
            while (i < len && chars[i] == '\n') {
                counter++;
                i++;
            }
            i++;
            if (statsMap.containsKey(counter))
                statsMap.put(counter, statsMap.get(counter) + 1);
            else
                statsMap.put(counter, 1);
        }
        if (statsMap.containsKey(0))
            statsMap.remove(0);
        float x = 0.01f;
        float y = 0.01f;
        for (Map.Entry<Integer, Integer> entry : statsMap.entrySet()) {
            x += entry.getKey() * entry.getValue();
            y += entry.getValue();
        }
        int newLineNum = Math.round(x / y);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < newLineNum; i++) {
            sb.append("\n");
        }
        String sep = sb.toString();
        return sep;
    }


    /**
     * Method for debug
     * @param chunks
     */
    private void printChunks(String[] chunks) {
        int digestLen = BIO_LENGTH_MAX;
        for (int i = 0; i < chunks.length; i++) {
            if (chunks[i].trim().isEmpty())
                continue;
            System.out.println("**Chunk PART: " + i);
            String digest = chunks[i];
            if (digest.length() > digestLen)
                digest = digest.substring(0, 100) + "...";
            System.out.println(digest + "\n");
        }
    }
}
