package me.app;

import me.utils.FileHandler;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SanDomingo
 * Date: 3/21/14
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class EduExtractorTest {
    @Test
    public void testExtract() {
//        List<String> bios = BioManager.readBiofile2List("artnetminer/bio.txt");
        List<String> bios = BioManager.readBioDB2List();
        EduExtractor extractor = new EduExtractor();
        for (String bio : bios) {
            String eduinfo = extractor.extract(bio);
            System.out.println("Edu Info: " + eduinfo);
        }
    }

    @Test
    public void testExtractFromString() {
        String bio = "A SHORT BIO.Yabo Xu joined School of Software, Sun Yat-sen University through \"A Hundred Elites Program\" in 2009.He earned his Ph.D. in Computer Science, Simon Fraser University, Canada in 2008. During his Ph.D. study, he worked as anintern in Microsoft Research Asia in 2005, and paid a research visit to CUHK and Microsoft AdCenter Lab in 2007 and 2008, respectively.Prior to this, he received his master degree at CUHK (Chinese University of Hong Kong) in 2003 andBachelor of Science degree in Computer Science from Nanjing University, China in 2001.\n";
        EduExtractor extractor = new EduExtractor();
            String eduinfo = extractor.extract(bio);
            System.out.println("Edu Info: " + eduinfo);
    }

    @Test
    public void testExtractFromHtml() throws Exception {
        String html = "html-bio/";
//        String html = "html-bio/emilio.html";
//        String html = "html-nbio/";
        File dir = new File(html);
        File[] files;
        if (!dir.isDirectory()) {
            files = new File[1];
            files[0] = dir;
        }
        else
            files = dir.listFiles();

        BioExtractor extractor = new BioExtractor();
        EduExtractor extractor1 = new EduExtractor();
        for (File file : files) {
            if (file.getName().endsWith("html") || (file.getName().endsWith("htm"))) {
                System.out.println("\n\n");
                String htmlstr = FileHandler.readFileToString(file.getAbsolutePath());
                String bio = extractor.extract(htmlstr);
                if (!bio.equals(BioExtractor.NO_BIO_FOUND)) {
                    String eduinfo = extractor1.extract(bio);
                    System.out.println(file.getName() + "==> " + eduinfo);
                }
            }
        }
    }
}
