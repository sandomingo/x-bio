package me.utils;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlUtils {
    private static final String stopwordsFile = "src/main/resources/stopwords.txt";
    private static final String biokeywordsFile = "src/main/resources/biowords.txt";
    private static final String fieldwordsFile = "src/main/resources/fieldwords.txt";
    private static final String degreewordsFile = "src/main/resources/degree.txt";
    private static final String homepageowrdsFile = "src/main/resources/homepagewords.txt";
    private static Set<String> fieldwords;
    private static Set<String> stopwords;
    private static Set<String> biokeywords;
    private static Set<String> degreewords;
    private static Set<String> homepagewords;
    private static SentenceDetectorME sdeector;
    static {
        loadStopwords();
        loadFieldwords();
        loadBioktopwords();
        loadDegreewords();
        loadHomepagewords();
        if (stopwords.isEmpty())
            System.err.println("load stopwords failed");
        if (biokeywords.isEmpty()) {
            System.err.println("load bio keywords failed");
        }

        try {
            InputStream is = new FileInputStream("src/main/resources/en-sent.bin");
            SentenceModel model = new SentenceModel(is);
            sdeector = new SentenceDetectorME(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HtmlUtils() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private static void loadDegreewords() {
        try {
            List<String> strlst = FileHandler.readFileToList(degreewordsFile);
            degreewords = new HashSet<String>();
            for (String str : strlst) {
                degreewords.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private static void loadStopwords() {
        try {
            List<String> strlst = FileHandler.readFileToList(stopwordsFile);
            stopwords = new HashSet<String>();
            for (String str : strlst) {
                stopwords.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private static void loadHomepagewords() {
        try {
            List<String> strlst = FileHandler.readFileToList(homepageowrdsFile);
            homepagewords = new HashSet<String>();
            for (String str : strlst) {
                homepagewords.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFieldword(String word) {
        return fieldwords.contains(word);
    }

    public static boolean isStopword(String word) {
        return stopwords.contains(word);
    }

    public static boolean isBiokeyword(String word) {
        return biokeywords.contains(word);
    }

    public static boolean isDegreeword(String word) {
        return degreewords.contains(word);
    }

    private static boolean hasHomepageword(String href) {
        for (String word : homepagewords) {
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
        text = text.replace("&nbsp;", " ").replace("&amp;", "&").replace("&quot;", "\"").
                replace("&lt;", "<").replace("&gt;", ">").replace("\t", " ");
        text = text.replaceAll("[ ]*\n[ ]*", "\n");
        return text;
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
            for (int i = 0; i < len; i++) {
                Element element = elements.get(i);
                String href = element.attr("href");
                // remove irrelevant link
                if (!HtmlUtils.hasHomepageword(href)) continue;
                // gen absolute path
                URL url = new URL(baseURL, href);
                href = url.toString();
                links.add(href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return links;
    }


    /**
     * 获取html文本内容，同时保留换行
     * @param bodyHtml
     * @return
     */
    public static String cleanPreserveLineBreaks(String bodyHtml) {
        Document document = Jsoup.parse(bodyHtml);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve line breaks and spacing
        String[] sepTags = new String[]{"br", "p", "hr", "table", "td", "tr", "h1", "h2", "h3", "h4"};
        for (String tag : sepTags) {
            document.select(tag).append("\\n");
        }
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
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
