package me.app;

import me.utils.FileHandler;
import me.utils.HtmlUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BioExtractorTest {
    @Test
    public void testExtracts() throws Exception {
//        String html = "html-bio/haifeng.html";
        String html = "html-bio/";
//        String html = "html-nbio/";
        File dir = new File(html);
        File[] files;
        if (!dir.isDirectory()) {
            files = new File[1];
            files[0] = dir;
        }
        else
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
        String htmlFilename = "html-bio/syu.html";
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
