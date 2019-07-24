package javadatatext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xschen on 9/10/15.
 */
public class LowerCase implements Serializable, TextFilter {

    private static final long serialVersionUID = 4206255149536921311L;


    @Override
    public List<String> filter(List<String> words) {
        List<String> result = new ArrayList<>();
        for(int i=0; i < words.size(); ++i){
            result.add(words.get(i).toLowerCase());
        }
        return result;
    }

    public LowerCase makeCopy(){
        return new LowerCase();
    }
}
