package javadatatext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by root on 11/5/15.
 */
public class PunctuationFilter implements TextFilter, Serializable
{
    private static final long serialVersionUID = 7117721387713659243L;
    private String filter="-{}[]";
    private List<String> stripFilter = new ArrayList<>();

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<String> getStripFilter() {
        return stripFilter;
    }

    public void setStripFilter(List<String> stripFilter) {
        this.stripFilter = stripFilter;
    }

    public PunctuationFilter(){
        stripFilter.add("\\{");
        stripFilter.add("\\}");
        stripFilter.add("\\[");
        stripFilter.add("\\]");
        stripFilter.add("\\(");
        stripFilter.add("\\)");
    }

    @Override
    public List<String> filter(List<String> words) {
        List<String> result = new ArrayList<>();
        for(String word : words){
            if(!isPunctuation(word)){
                result.add(strip(word));
            }
        }
        return result;
    }

    private boolean isPunctuation(String w){
        w = w.trim();
        if(w.length() > 1) return false;

        return filter.contains(w);
    }

    private String strip(String word){
        for(int i=0; i < stripFilter.size(); ++i){
            word = word.replaceAll(stripFilter.get(i), "");
        }
        return word;
    }

    @Override
    public Object clone(){
        PunctuationFilter clone = new PunctuationFilter();
        clone.copy(this);
        return clone;
    }

    public void copy(PunctuationFilter rhs){
        stripFilter.clear();
        stripFilter.addAll(rhs.stripFilter);

        filter = rhs.filter;
    }
}
