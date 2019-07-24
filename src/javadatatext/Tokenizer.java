package javadatatext;

import java.util.List;


/**
 * Created by xschen on 9/10/15.
 */
public interface Tokenizer {
    List<String> tokenize(String text);
}
