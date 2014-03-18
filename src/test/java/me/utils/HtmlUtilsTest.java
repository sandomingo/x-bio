package me.utils;

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
        String bio = "Heikki Mannila received his Ph.D. in computer science in 1985 from the University of Helsinki. After some time at the University of Tampere and various researcher positions, in 1989 he was appointed a professor of computer science at the University of Helsinki. He was a visiting professor in the Technical University of Vienna in 1993 and a visiting researcher at Max Planck Institute for Computer Science in Saarbruecken in 1995-96. He moved to Microsoft Research in Redmond in 1998, came back to Finland to Nokia Research in 1999, where he stayed until the end of 2001. After that, Heikki Mannila was the research director of the basic research unit of Helsinki Institute for Information Technology in 2002-2004. Since 1999 he is a professor of computer science at Helsinki University of Technology. In 2004-2008 he was an academy professor. In 2009 he was the director of Helsinki Institute for Information Technology HIIT, a joint research institute of University of Helsinki and Helsinki University of Technology. In 2009-2012 he was vice president for academic affairs at Aalto University, a new university formed by the merger of Helsinki University of Technology, Helsinki School of Economics, and University of Art and Design Helsinki. Currently, he is president of the Academy of Finland.\n";
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
        FileHandler.writeListToFile(lst, "out.txt");
        System.out.println(text);
    }
}
