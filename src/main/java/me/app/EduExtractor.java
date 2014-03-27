package me.app;

import me.utils.HtmlUtils;
import me.utils.NER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: SanDomingo
 * Date: 3/20/14
 * Time: 5:12 PM
 */
public class EduExtractor {
    private static String orgll = "<ORGANIZATION>";
    private static String orgrl = "</ORGANIZATION>";
    private static int orgllen = orgll.length();
    private static EduExtractor instance;
    public static EduExtractor getInstance() {
        if (instance == null) {
            instance = new EduExtractor();
        }
        return instance;
    }
    /**
     * 从给定的个人简介中，抽取出一个人的最高教育经历，包括学位和取得学位时所在学校
     * @param bio
     * @return
     */
    public String extract(String bio) {
        List<EduInfo> eduInfos = new ArrayList<EduInfo>();
        List<String> sentences = HtmlUtils.splitTxt(bio);
        for (String sentence : sentences) {
            List<EduInfo> einfos = extractEduInfo(sentence);
            if (!einfos.isEmpty()) {
                for (EduInfo einfo : einfos) {
                    eduInfos.add(einfo);
                }
            }
        }
        // 按学历从高到低排序
        Collections.sort(eduInfos, new Comparator<EduInfo>() {
            @Override
            public int compare(EduInfo o1, EduInfo o2) {
                return o1.rank - o2.rank;
            }
        });

        // 将学历信息转换成一个字符串
        List<String> result = new ArrayList<String>();
        for (EduInfo eduInfo : eduInfos) {
            result.add(eduInfo.toString());
        }
        return result.isEmpty() ? "None" : result.get(0);
    }

    /**
     * 从简历中的一句话中抽取其个人教育经历。
     * @param sentence
     * @return
     */
    private List<EduInfo> extractEduInfo(String sentence) {
        String eduStr = NER.getInstance().classify(sentence);
        List<EduInfo> eduInfos = new ArrayList<EduInfo>();
        List<String> schools = getORG(eduStr);
        // find degree next to the school
        List<String> degrees = new ArrayList<String>();
        List<String> words = HtmlUtils.token(sentence);
        for (String word : words) {
            if (HtmlUtils.isDegreeword(word)) {
                degrees.add(word);
            }
        }

        int degreeNum = degrees.size();
        int schoolNum = schools.size();
        // solve the case like: I got my M.S and Ph.D from SYSU.
        // Solution: simply padding last school to make degrees' size equal to schools'
        if (degreeNum > schoolNum && schoolNum > 0) {
            int paddingNum = degreeNum - schoolNum;
            for (int i = 0; i < paddingNum; i++) {
                schools.add(schools.get(schools.size()-1));
                schoolNum += 1;
            }
        }
        int minNum = schoolNum > degreeNum ? degreeNum : schoolNum;

        for (int i = 0; i < minNum; i++) {
            EduInfo eduInfo = new EduInfo(schools.get(i), degrees.get(i));
            eduInfos.add(eduInfo);
        }


        return eduInfos;
    }

    /**
     * find organization in the txt
     * @param txt
     * @return
     */
    private static List<String> getORG(String txt) {
        List<String> orgs = new ArrayList<String>();
        int head = 0, rear;
        while (head < txt.length()) {
            head = txt.indexOf(orgll, head);
            if (head < 0) {
                break;
            }
            head += orgllen;
            rear = txt.indexOf(orgrl, head);
            String school = txt.substring(head, rear);
            String schoolLowerCase = school.toLowerCase();
            if (schoolLowerCase.contains("university") || schoolLowerCase.contains("institute")
                    || schoolLowerCase.contains("research") || schoolLowerCase.contains("UC"))
                orgs.add(school);
        }
        return orgs;
    }

    public static class EduInfo{
        String degree;
        String school;
        Integer rank; // Ph.D->1, M.S->2, B.S->3

        public EduInfo(String school, String degree) {
            this.school = school;
            this.degree = degree;
            if (degree.contains("p")) {
                this.rank = 1;
            } else if (degree.contains("m")) {
                this.rank = 2;
            } else {
                this.rank = 3;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (degree != null && !degree.isEmpty())
                sb.append(degree).append("\t");
            if (school != null && !school.isEmpty())
                sb.append(school);
            return sb.toString().trim();
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }
    }
}
