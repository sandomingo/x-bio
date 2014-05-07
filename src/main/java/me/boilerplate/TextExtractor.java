package me.boilerplate;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import me.utils.FileHandler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by SanDomingo on 4/15/14.
 */
public class TextExtractor {
    public static void testArticleExtractor() {
        try {
            File dir = new File("html-bio");
            File[] files = dir.listFiles();
            int counter = 1;
            for (File file : files) {
                String html = FileHandler.readFileToString(file.getAbsolutePath());
                html = ArticleExtractor.INSTANCE.getText(html);
                String para[] = html.split("\n");
                int lineNO = 0;
                for (String txt : para) {
                    System.out.println(++lineNO + "\t" + txt);
                }
                if (--counter <= 0) {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TextExtractor.testArticleExtractor();
    }
}
