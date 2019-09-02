package javadatatext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xschen on 14/8/15.
 */
public class BasicVocabulary implements Serializable, Vocabulary {
    private static final long serialVersionUID = -4386706542135437234L;
    private List<String> words;

    public BasicVocabulary(List<String> words) {
        this.words = words;
    }

    public BasicVocabulary() {
        words = new ArrayList<>();
    }

    public int getLength() {
        return words.size();
    }

    public String get(int index) {
        return words.get(index);
    }

    public boolean contains(String word) {
        return words.indexOf(word) != -1;
    }

    public void add(String word) {
        words.add(word);
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    @Override
    public Vocabulary makeCopy() {
        BasicVocabulary clone = new BasicVocabulary(clone(words));
        return clone;
    }

    private List<String> clone(List<String> rhs) {
        List<String> clone = new ArrayList<>();
        clone.addAll(rhs);
        return clone;
    }

    public int indexOf(String word) {
        return words.indexOf(word);
    }
}
