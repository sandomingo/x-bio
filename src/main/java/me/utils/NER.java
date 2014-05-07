package me.utils;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Name Entity Recognition Class
 * User: SanDomingo
 * Date: 3/21/14
 * Time: 10:37 AM
 */
public class NER {
    private static String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
    private static NER instance;
    private AbstractSequenceClassifier<CoreLabel> classifier;
    private NER() {
        classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
    }

    public static NER getInstance() {
        while (instance == null) {
            instance = new NER();
        }
        return instance;
    }

    public String classify(String sentence) {
        String result = classifier.classifyWithInlineXML(sentence);
        return result;
    }
}
