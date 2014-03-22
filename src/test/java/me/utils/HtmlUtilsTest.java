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
        String filename = "html-bio/atul.html";
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
        String bio = "Ying-Cheng Lai received B.S. and M.S. degrees in Optical Engineering from Zhejiang University in 1982 and 1985, and M.S. and Ph.D. degrees in Physics from University of Maryland at College Park in 1989 and 1992, respectively. He wrote his Ph.D. thesis on Classical and Quantum Chaos under Celso Grebogi, James A. Yorke and Edward Ott. From 1992-1994 he was a post-doctoral fellow in the Biomedical Engineering Department at the Johns Hopkins University School of Medicine under Raimond Winslow and Murray Sachs. He joined the University of Kansas in 1994 as an Assistant Professor of Physics and Mathematics and became Associate Professor in 1998. In 1999, he came to Arizona State University as Associate Professor of Mathematics and Associate Professor of Electrical Engineering. He was promoted to Professor of Mathematics and Professor of Electrical Engineering in 2001. In 2005, he switched full-time into Electrical Engineering. In 2009, Y.-C. Lai was named the Sixth Century Chair in Electrical Engineering by the University of Aberdeen, Scotland, UK. In January 2014, he was awarded the ISS Chair Professorship of Electrical Engineering at Arizona State University.\n";
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
