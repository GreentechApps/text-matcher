package javadatatext;


import java.util.List;


/**
 * Created by xschen on 15/5/2017.
 */
public interface TextFilter {
   List<String> filter(List<String> words);
}
