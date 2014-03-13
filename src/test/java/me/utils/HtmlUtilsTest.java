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

    }

    @Test
    public void testIsBiokeyword() throws Exception {

    }

    @Test
    public void testGetText() throws Exception {
        String filename = "html-slicer/html/yabo.html";
        String html = FileHandler.readFileToString(filename);
        String text = HtmlUtils.getText(html);
        List<String> lst = new ArrayList<String>();
        lst.add(text);
        FileHandler.writeListToFile(lst, "out.txt");
        System.out.println(text);
    }
}
