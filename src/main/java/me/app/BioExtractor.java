package me.app;

import me.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 个人简历抽取类
 *
 * 抽取算法：
 * 1. 使用home page的url下载该用户的个人主页
 * 2. 在个人主页中寻找并抽取个人简历
 *  2.1 将html页面的纯文本抽取出来，并用换行符替代去掉的块级标签
 *  2.2 计算该文档的平均换行数，用来将文本切块
 *  2.3 对于每一块文字，按关键词匹配打分
 *  2.4 选择打分超过一定阈值(SCORE)的块，拼接成个人简历。
 * 3. 如果该页中没有，则取当前页面所有的二级页面链接重复上述操作
 * 4. 如果二级页面中也没有，则结束
 *
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 6:18 PM
 */
public class BioExtractor {
    private static Logger logger = LoggerFactory.getLogger(BioExtractor.class);
    public static final int BIO_LENGTH_MIN = 15;
    public static final int BIO_LENGTH_MAX = 800;
    private static final int SCORE = 14;
    private static final int PUB_SCORE = 9; // 论文指数
    public static final String NO_BIO_FOUND = "No bio found.";
    private static BioExtractor instance;

    public static BioExtractor getInstance() {
        if (instance == null) {
            instance = new BioExtractor();
        }
        return instance;
    }

    public BioExtractor() {
    }

    /**
     * 从任一个人Homepage html文档中抽取出个人简介
     * @param htmlString
     * @return
     */
    public String extract(String htmlString) {
        String text = HtmlUtils.getText(htmlString);
        List<String> chunks = sliceTest(text);
        String bio = selectBio(chunks);
        // prevent wrong sentence segmentation
        if (!bio.isEmpty())
            bio = bio.replace("Ph. D", "Ph.D");
        return bio;
    }

    /**
     * 简单的按行切块，并且去除空白行
     * @param text
     * @return
     */
    private List<String> sliceTest(String text) {
        String[] chunks = text.split("\n");
        List<String> result = new ArrayList<String>();
        for (String chunk : chunks) {
            chunk = chunk.trim();
            if (chunk.isEmpty()) {
                continue;
            }
            result.add(chunk);
        }
        return result;
    }

    /**
     * 根据关键词在文本中出现的频率选取最有可能是个人简介的文本。
     * @param chunks
     * @return 返回bio，bio为空字符串时则是因为没有bio
     */
    private String selectBio(List<String> chunks) {
        Map<Integer, List<String>> refinedChunkMap = new TreeMap<Integer, List<String>>(); // key:chunk id, value: chunk tokens
        int len = chunks.size();
        Set<Integer> mustBeBio = new HashSet<Integer>(); // 肯定为bio的chunk的编号
        for (int i = 0; i < len; i++) {
            String chunk = " " + chunks.get(i); // for mustBeBio contains operation
            // 去除不是已句号结尾的段落。从而获得完整的句子。
            if (!chunk.endsWith(".")) {
                continue;
            }
            // 分词
            List<String> words = HtmlUtils.token(chunks.get(i));
            if (chunk.contains(" am ") || chunk.contains(" I'm ")) {
                mustBeBio.add(i);
            } else {
                // 去除用词过短的文本
                if (words.size() < BIO_LENGTH_MIN || words.size() > BIO_LENGTH_MAX)
                    continue;
            }
            refinedChunkMap.put(i, words);
        }

        // build an bio
        StringBuilder bioBuilder = new StringBuilder();
        StringBuilder hitWordsBuilder;
        for (Map.Entry<Integer, List<String>> entry : refinedChunkMap.entrySet()) {
//            hitWordsBuilder = new StringBuilder();
            double score = 0;
            if (mustBeBio.contains(entry.getKey())) {
                score = SCORE;
            } else {
                for (String word : entry.getValue()) {
                    if (HtmlUtils.isBiokeyword(word)) {
                        score += 1;
//                        hitWordsBuilder.append(word).append(",");
                    }
                }
                score = score * 100 / entry.getValue().size();
            }
            if (score >= SCORE) {
                if (!mustBeBio.contains(entry.getKey())) {
                    // 数标点符号个数
                    int numNum = countNumbers(chunks.get(entry.getKey()));
                    // 去除发表论文的内容：依赖于句子数字出现频率，频率越高，越有可能是论文。
                    double pubScore = 100 * numNum / entry.getValue().size();
//                    logger.info(score + " | " + pubScore + " ==> " + chunks.get(entry.getKey()));
                    if (pubScore > PUB_SCORE) {
                        continue;
                    }
                }
//                System.out.println("Hiting: " + hitWordsBuilder.toString());
                bioBuilder.append(chunks.get(entry.getKey()) + "\n");
            }
        }
        String bioStr = bioBuilder.toString();

        // remove extra space
        bioStr = HtmlUtils.removeExtraSpace(bioStr);
        return bioStr.trim();
    }


    /**
     * 数一段文本中标点符号出现的个数
     * @param txt
     * @return
     */
    private int countNumbers(String txt) {
        int count = 0;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(txt);
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
