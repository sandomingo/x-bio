package me.app;

import me.utils.FileHandler;
import me.utils.HtmlUtils;
import me.utils.HtmlUtilsTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BioExtractorTest {
    @Test
    public void testExtractByBoilerplate() {
        try {
            File dir = new File("html-bio");
            File[] files = dir.listFiles();
            int counter = 1;
            for (File file : files) {
                String html = FileHandler.readFileToString(file.getAbsolutePath());
                String bio = BioExtractor.getInstance().extractWithBoilerplate(html);
                System.out.println(bio);
                if (--counter <= 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testExtract2() {
        String url = "http://web.eee.sztaki.hu/~kla/index.html";
        String htmlString = HtmlUtilsTest.getHTML(url, "utf-8"); // download homepage
        String bio = BioExtractor.getInstance().extract(htmlString);

        //if found no bio in homepage, then fetch the next level pages
        if (bio.equals(BioExtractor.NO_BIO_FOUND)) {
            List<String> bioPageUrls = HtmlUtils.getLinks(htmlString, url);
            for (String bioPageUrl : bioPageUrls) {
                String nextPageHtmlString = HtmlUtilsTest.getHTML(bioPageUrl, "utf-8");
                bio = BioExtractor.getInstance().extract(nextPageHtmlString);
                if (!bio.isEmpty()) break;
            }
        }
        if (bio.equals(BioExtractor.NO_BIO_FOUND)) {
            System.out.println("None Bio");
        } else {
            System.out.println("Bio: " + bio);
        }

    }

    @Test
    public void testExtracts() throws Exception {
//        String html = "html-bio/binbin.html";
//        String html = "html-bio/";
//        String html = "html-nbio/";
        String html = "2";
        File dir = new File(html);
        File[] files;
        if (!dir.isDirectory()) {
            files = new File[1];
            files[0] = dir;
        } else
            files = dir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("html") || (file.getName().endsWith("htm"))) {
                System.out.println("####" + file.getName() + "####");
                testExtract(file.getAbsolutePath());
                System.out.println();
            }
        }
    }

    public void testExtract(String htmlFilename) throws Exception {
        String html = FileHandler.readFileToString(htmlFilename);
        BioExtractor extractor = new BioExtractor();
        String bio = extractor.extract(html);
        System.out.println(bio);
    }

    @Test
    public void testSliceText() throws IOException {
        String htmlFilename = "html-bio/abdel.html";
        String html = FileHandler.readFileToString(htmlFilename);
        BioExtractor extractor = new BioExtractor();
        String text = HtmlUtils.getText(html);
        String[] chunks = extractor.sliceText(text);
        int i = 0;
        for (String str : chunks) {
            System.out.println("########Part " + ++i + " ##########");
            System.out.println(str);
        }
    }

    @Test
    public void testSliceText2() throws IOException {
        BioExtractor extractor = new BioExtractor();
//        String text = "a a aa  aa  aaa   aaa";
        String text = "<a>w)     TEACHING</a>";
        text = HtmlUtils.getText(text);
        String[] chunks = extractor.sliceText(text);
        int i = 0;
        for (String str : chunks) {
            System.out.println("########Part " + ++i + " ##########");
            System.out.println(str);
        }
    }
}
