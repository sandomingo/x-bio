package me.app;

import com.yeezhao.commons.util.sql.BaseDao;
import com.yeezhao.commons.util.sql.BaseDaoFactory;
import me.utils.FileHandler;
import me.utils.HtmlUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * 用来从收集好的个人简历中采集关键词
 * 简历文件格式为：
 *      ＃ Name 1
 *      Text (may across multiple lines)
 *      ＃ Name 2
 *      Text (may across multiple lines)
 * User: SanDomingo
 * Date: 3/12/14
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class BioManager {
    private List<String> bios;
    public static final int TOP_N = 1000;
    private Map<String, Integer> keywordsMap; // keyword : occur number

    public BioManager(String biofile) {
        keywordsMap = new HashMap<String, Integer>();
        loadBios(biofile);
        selectKws(TOP_N);
    }

    /**
     * 从简历文件中选择关键词
     */
    private void selectKws(int topN) {
        Map<String, Integer> wordsMap = new TreeMap<String, Integer>();
        List<Integer> occurrence = new LinkedList<Integer>();
        for (String bio : bios) {
            List<String> bioInWords = HtmlUtils.token(bio);
            bioInWords = removeStopWords(bioInWords);
            for (String word : bioInWords) {
                if (wordsMap.containsKey(word))
                    wordsMap.put(word, wordsMap.get(word) + 1);
                else
                    wordsMap.put(word, 1);
            }
        }
        for (Integer num : wordsMap.values()) {
            occurrence.add(num);
        }
        Collections.sort(occurrence);
        int target = 0;
        int counter = 0;
        for (int i = occurrence.size() - 1; i >= 0; i--) {
             counter++;
            if (counter == topN) {
                target = occurrence.get(i);
            }
        }
        for (Map.Entry<String, Integer> entry : wordsMap.entrySet()) {
            if (entry.getValue() >= target) {
                keywordsMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private List<String> removeStopWords(List<String> bioInWords) {
        List<String> biowords = new ArrayList<String>();
        for (String word : bioInWords) {
            if (word.length() < 2 || HtmlUtils.isStopword(word)) {
                continue;
            }
            biowords.add(word);
        }
        return biowords;
    }

    /**
     * 从指定个人简介文件中加载个人简介
     * @param biofile
     */
    private void loadBios(String biofile) {
//        List<String> biolst = readBiofile2List(biofile);
        List<String> biolst = readBioDB2List();
        this.bios = biolst;
    }

    private List<String> readBioDB2List() {
        List<String> bios = new ArrayList<String>();
        String url = "jdbc:mysql://localhost/db_yeezhao_hound|hound|123456";
        BaseDao dao = BaseDaoFactory.getDaoBaseInstance(url);
        String sql = "SELECT bio FROM db_yeezhao_hound.arnet1;";
        try {
            bios = dao.queryColumns(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bios;
    }
    /**
     * 将符合格式Bio文件按条读入
     * @param biofile
     * @return
     */
    private List<String> readBiofile2List(String biofile) {
        List<String> bios = new ArrayList<String>();
        List<String> lines = null;
        try {
            lines = FileHandler.readFileToList(biofile);
            StringBuilder sb = new StringBuilder();
            for (String str : lines) {
                str = str.trim();
                // skip a comment or an empty line
                if (str.startsWith("#") || str.isEmpty()) {
                    String abio = sb.toString();
                    if (!abio.isEmpty()) {
                        bios.add(abio);
                        sb.delete(0, sb.length() - 1);
                        continue;
                    }
                }
                sb.append(str);
            }
            // add the last bio
            if (sb.length() != 0) {
                bios.add(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bios;
    }

    /**
     * 从个人简介中根据出现频率挑选关键词，
     * 其中关键词不包括stop word
     */
    public List<String> getKeyWords() {
        List<String> kws = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : keywordsMap.entrySet()) {
            kws.add(entry.getKey());
        }
        return kws;
    }



    /**
     * 持久话到文本
     * @param outfile
     */
    public void saveBioKeywords(String outfile) {
        List<String> kws = getKeyWords();
        try {
            FileHandler.writeListToFile(kws, outfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String biofile = "artnetminer/arnetminer.txt";
        BioManager bioManager = new BioManager(biofile);
        bioManager.readBioDB2List();
        bioManager.saveBioKeywords("biokw.txt");
    }
}
