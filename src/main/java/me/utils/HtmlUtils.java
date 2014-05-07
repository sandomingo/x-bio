package me.utils;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 6:06 PM
 */
public class HtmlUtils {
    private static Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    private static final String biokeywordsFile = "bio/biowords.txt";
    private static final String fieldwordsFile = "bio/fieldwords.txt";
    private static final String degreewordsFile = "bio/degree.txt";
    private static final String homepageowrdsFile = "bio/homepagewords.txt";
    private static final String ensentFile = "bio/en-sent.bin";
    private static Set<String> fieldwords;
    private static Set<String> biokeywords;
    private static Set<String> degreewords;
    private static Set<String> homepagewords;
    private static SentenceDetectorME sdeector;
    private static final String[] searchList = new String[]{"&nbsp;", "&amp;", "&quot;", "&lt;", "&gt;", "\t", "\r"};
    private static final String[] replacementList = new String[]{" ", "&", "\"", "<", ">", " ", ""};
    private static final Document.OutputSettings prettyOutput = new Document.OutputSettings().prettyPrint(true);
    private static final Document.OutputSettings notPrettyOutput = new Document.OutputSettings().prettyPrint(false);
    static {
        loadFieldwords();
        loadBioktopwords();
        loadDegreewords();
        loadHomepagewords();
        if (biokeywords.isEmpty()) {
            logger.error("load bio keywords failed!");
        }

        try {
            InputStream is = new FileInputStream(ensentFile);
            SentenceModel model = new SentenceModel(is);
            sdeector = new SentenceDetectorME(model);
        } catch (Exception e) {
            logger.error(e.getMessage() + "load en-sent file failed!");
        }
    }

    private static void loadHomepagewords() {
        try {
            List<String> strlst = FileHandler.readFileToList(homepageowrdsFile);
            homepagewords = new HashSet<String>();
            for (String str : strlst) {
                homepagewords.add(str.toLowerCase());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void loadDegreewords() {
        try {
            List<String> strlst = FileHandler.readFileToList(degreewordsFile);
            degreewords = new HashSet<String>();
            for (String str : strlst) {
                degreewords.add(str);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void loadFieldwords() {
        try {
            List<String> strlst = FileHandler.readFileToList(fieldwordsFile);
            fieldwords = new HashSet<String>();
            for (String str : strlst) {
                fieldwords.add(str);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void loadBioktopwords() {
        try {
            List<String> strlst = FileHandler.readFileToList(biokeywordsFile);
            biokeywords = new HashSet<String>();
            for (String str : strlst) {
                if (!isFieldword(str))
                    biokeywords.add(str);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static boolean isFieldword(String word) {
        return fieldwords.contains(word);
    }

    public static boolean isBiokeyword(String word) {
        return biokeywords.contains(word);
    }

    public static boolean isDegreeword(String word) {
        return degreewords.contains(word);
    }

    private static boolean hasHomepageword(String href) {
        for (String word : homepagewords) {
            href = href.toLowerCase();
            if (href.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 抽取html中包含的全部文本
     * @param html
     * @return
     */
    public static String getText(String html) {
        String text = cleanPreserveLineBreaks(html);
        text = StringUtils.replaceEach(text, searchList, replacementList);
        text = text.replaceAll("[ ]*\n[ ]*", "\n");
        return text;
    }


    /**
     * 获取html文本内容，同时保留换行
     * @param bodyHtml
     * @return
     */
    public static String cleanPreserveLineBreaks(String bodyHtml) {
        bodyHtml = bodyHtml.replaceAll("[\n\r]", " ");
        Document document = Jsoup.parse(bodyHtml);
        document.outputSettings(notPrettyOutput);
        String[] firstClassTags = new String[]{"br", "p", "hr", "table", "td", "tr", "h1", "h2", "h3", "h4", "ul", "ol", "div"};
        for (String tag : firstClassTags) {
            document.select(tag).append("\n\n");
        }
        String[] secondClassTags = new String[]{"li"};
        for (String tag : secondClassTags) {
            document.select(tag).append("\n");
        }
        return Jsoup.clean(document.html(), "", Whitelist.none(), notPrettyOutput);
    }

    public static String removeExtraSpace(String sent) {
        return sent.replaceAll(" +", " ");
    }

    /**
     * 段落文本断句
     * @param txt
     * @return
     */
    public static List<String> splitTxt(String txt) {
        String sentence[] = sdeector.sentDetect(txt);
        return Arrays.asList(sentence);
    }

    /**
     * 抽取一个页面的所有二级页面的链接
     * @param html
     * @return
     */
    public static List<String> getLinks(String html, String base) {
        List<String> links = new ArrayList<String>();
        try {
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByTag("a");
            int len = elements.size();
            URL baseURL = new URL(base);
            base = base.substring(0, base.lastIndexOf("/"));
            for (int i = 0; i < len; i++) {
                Element element = elements.get(i);
                String href = element.attr("href");
                // remove irrelevant link
                if (!HtmlUtils.hasHomepageword(href)) {
                    logger.debug("Omitted link(No hp word): " + href);
                    continue;
                }
                // gen absolute path
                URL url = new URL(baseURL, href);
                href = url.toString();
                if (!href.contains(base)) {
                    logger.debug("Omitted link(out link): " + href);
                    continue; // skip out link
                }
                links.add(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return links;
    }

    /**
     * 分词，字母变小写并尝试去除单词后面的标点符号(',', '.')
     * @param bio
     * @return
     */
    public static List<String> token(String bio) {
        List<String> tokens =  token(new StringReader(bio));
        int len = tokens.size();
        for (int i = 0; i < len; i++) {
            String token = tokens.get(i);
            while (token.endsWith(",") || token.endsWith(".")) {
                token = token.substring(0, token.length()-1);
            }
            tokens.set(i, token);
        }
        return tokens;
    }

    private static List<String> token(Reader reader) {
        IKSegmenter segment = new IKSegmenter(reader, true);
        ArrayList<String> tokens = new ArrayList<String>();
        Lexeme lexeme;
        try {
            while ((lexeme = segment.next()) != null) {
                tokens.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
        }
        return tokens;
    }
}
