package me.utils;

import me.app.BioManager;
import me.app.EduExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SanDomingo
 * Date: 3/10/14
 * Time: 8:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class HtmlUtilsTest {
    @Test
    public void testIsStopword() throws Exception {
        String a = "d";
        System.out.println(HtmlUtils.isStopword(a));
    }

    @Test
    public void testIsBiokeyword() throws Exception {
        String kw = "researcher";
        System.out.println(HtmlUtils.isBiokeyword(kw));
    }

    @Test
    public void testToken() throws Exception {
        String bio = "I amcurrently a Postdoctral ResearchAssociate of theCenterfor Evolutionary Medicine and Informatics (CEMI) of theBiodesign Institute at ASU. My cooperative supervisor is Prof. JiepingYe. Before joining ASU in March 2013, I received the B.S.degree in mathematics and the Ph.D. degree in computer science fromZhejiang University, Hangzhou, China, in 2007 and 2012, respectively.";
        List<String> lst = HtmlUtils.token(bio);
        TestUtils.printStringList(lst);
    }

    @Test
    public void testGetText() throws Exception {
        String filename = "html-bio/yabo.html";
        String html = FileHandler.readFileToString(filename);
        String text = HtmlUtils.getText(html);
        List<String> lst = new ArrayList<String>();
        lst.add(text);
//        FileHandler.writeListToFile(lst, "out.txt");
        System.out.println(text);
    }

    @Test
    public void testSplitTxt() {
        List<String> bios = BioManager.readBiofile2List("artnetminer/bio.txt");
        for (String bio : bios) {
            List<String> sentences = HtmlUtils.splitTxt(bio);
            System.out.println("####");
            printList(sentences);
            System.out.println("####");
        }

    }
    @Test
    public void testSplitTxt2() {
        String bio = "A SHORT BIO.  Yabo Xu joined School of Software, Sun Yat-sen University through \"A Hundred Elites Program\" in 2009.  He earned his Ph.D. in Computer Science, Simon Fraser University, Canada in 2008. During his Ph.D. study, he worked as an  intern in Microsoft Research Asia in 2005, and paid a research visit to CUHK and Microsoft AdCenter Lab in 2007 and 2008, respectively.  Prior to this, he received his master degree at CUHK (Chinese University of Hong Kong) in 2003 and  Bachelor of Science degree in Computer Science from Nanjing University, China in 2001.";
        List<String> sentences = HtmlUtils.splitTxt(bio);
        System.out.println("####");
        printList(sentences);
        System.out.println("####");
    }

    @Test
    public void testRemoveExtraSpace() {
        String txt = "Automatic extraction and representation of visual concepts and           semantic information in scene is a desired capability in any security           and surveillance operations. In this project we target the problem of           visual event recognition in network information environment, where           faulty sensors, lack of effective visual processing tools and           incomplete domain knowledge frequently cause uncertainty in the data           set and consequently, in the visual primitives extracted from it. We           adopt Markov Logic Network (MLN), that combines probabilistic           graphical models and first order logic, to address the task of           reasoning under uncertainty. MLN is a knowledge representation           language that combines domain knowledge, visual concepts and           experience to infer simple and complex real-world events. MLN           generalizes over the existing state-of-the-art probabilistic models,           including hidden Markov models, Bayesian networks, and stochastic           grammars. Moreover, the framework can be made scalable to support           variety of entities, activities and interactions that are typically           observed in the real world.";
        System.out.println(HtmlUtils.removeExtraSpace(txt));
    }
    private void printList(List<String> sentences) {
        for (String sent : sentences) {
            System.out.println("==>>" + sent);
        }
    }
}
