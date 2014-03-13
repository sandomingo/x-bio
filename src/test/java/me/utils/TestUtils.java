package me.utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SanDomingo
 * Date: 3/12/14
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestUtils {
    public static void printStringList(List<String> lst) {
        for (String str : lst) {
            System.out.println("->" + str + "<-");
        }
    }
}
