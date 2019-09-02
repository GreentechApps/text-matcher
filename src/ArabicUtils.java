import java.io.IOException;
import java.text.Collator;
import java.util.*;

public class ArabicUtils {

    public static String arabic_punctuations = "''`÷×؛<>_()*&^%][ـ،/:\"؟.,'{}~¦+|!”…“–ـ''";
    // I don't know what this means, i just copy and paste it from internet
    private static String source = "یبدو أن شکوک الأمس قد تبخرت وأنت الآن منشغل بالتقدم للأمام؛ حاول " +
            "استخدام هذه الطاقه فی البدء فی مشروعات جدیده واتخاذ قرارات هامه. بما أن الأمور تسیر فی الاتجاه" +
            " الصحیح، وأنت تشعر بالراحه بالنسبه للأمور التی تقوم بها، یمکنک الاستمتاع بالنجاح القادم. لکن" +
            " لا ترتبط بالکثیر من المشروعات وذلک لأن توازنک الداخلی سوف یصبح مهددًا وسوف تتعرض لخطر استنفاذ نفسک.";
    private static String sw = "فی";

    /**
     * A logger for this class
     */
//  private static Redwood.RedwoodChannels log = Redwood.channels(ArabicUtils.class);
    public static Map<String, String> presToLogicalMap() {
        Map<String, String> rules = new HashMap<>();

        // PRESENTATION FORM TO LOGICAL FORM NORMALIZATION (presentation form is rarely used - but some UN documents have it).
        rules.put("\\ufc5e", "\u0020\u064c\u0651"); // ligature shadda with dammatan isloated
        rules.put("\\ufc5f", "\u0020\u064d\u0651"); // ligature shadda with kasratan isloated
        rules.put("\\ufc60", "\u0020\u064e\u0651"); // ligature shadda with fatha isloated
        rules.put("\\ufc61", "\u0020\u064f\u0651"); // ligature shadda with damma isloated
        rules.put("\\ufc62", "\u0020\u0650\u0651"); // ligature shadda with kasra isloated
        // Arabic Presentation Form-B to Logical Form
        rules.put("\\ufe80", "\u0621"); // isolated hamza
        rules.put("[\\ufe81\\ufe82]", "\u0622"); // alef with madda
        rules.put("[\\ufe83\\ufe84]", "\u0623"); // alef with hamza above
        rules.put("[\\ufe85\\ufe86]", "\u0624"); // waw with hamza above
        rules.put("[\\ufe87\\ufe88]", "\u0625"); // alef with hamza below
        rules.put("[\\ufe89\\ufe8a\\ufe8b\\ufe8c]", "\u0626"); // yeh with hamza above
        rules.put("[\\ufe8d\\ufe8e]", "\u0627"); // alef
        rules.put("[\\ufe8f\\ufe90\\ufe91\\ufe92]", "\u0628"); // beh
        rules.put("[\\ufe93\\ufe94]", "\u0629"); // teh marbuta
        rules.put("[\\ufe95\\ufe96\\ufe97\\ufe98]", "\u062a"); // teh
        rules.put("[\\ufe99\\ufe9a\\ufe9b\\ufe9c]", "\u062b"); // theh
        rules.put("[\\ufe9d\\ufe9e\\ufe9f\\ufea0]", "\u062c"); // jeem
        rules.put("[\\ufea1\\ufea2\\ufea3\\ufea4]", "\u062d"); // haa
        rules.put("[\\ufea5\\ufea6\\ufea7\\ufea8]", "\u062e"); // khaa
        rules.put("[\\ufea9\\ufeaa]", "\u062f"); // dal
        rules.put("[\\ufeab\\ufeac]", "\u0630"); // dhal
        rules.put("[\\ufead\\ufeae]", "\u0631"); // reh
        rules.put("[\\ufeaf\\ufeb0]", "\u0632"); // zain
        rules.put("[\\ufeb1\\ufeb2\\ufeb3\\ufeb4]", "\u0633"); // seen
        rules.put("[\\ufeb5\\ufeb6\\ufeb7\\ufeb8]", "\u0634"); // sheen
        rules.put("[\\ufeb9\\ufeba\\ufebb\\ufebc]", "\u0635"); // sad
        rules.put("[\\ufebd\\ufebe\\ufebf\\ufec0]", "\u0636"); // dad
        rules.put("[\\ufec1\\ufec2\\ufec3\\ufec4]", "\u0637"); // tah
        rules.put("[\\ufec5\\ufec6\\ufec7\\ufec8]", "\u0638"); // zah
        rules.put("[\\ufec9\\ufeca\\ufecb\\ufecc]", "\u0639"); // ain
        rules.put("[\\ufecd\\ufece\\ufecf\\ufed0]", "\u063a"); // ghain
        rules.put("[\\ufed1\\ufed2\\ufed3\\ufed4]", "\u0641"); // feh
        rules.put("[\\ufed5\\ufed6\\ufed7\\ufed8]", "\u0642"); // qaf
        rules.put("[\\ufed9\\ufeda\\ufedb\\ufedc]", "\u0643"); // kaf
        rules.put("[\\ufedd\\ufede\\ufedf\\ufee0]", "\u0644"); // ghain
        rules.put("[\\ufee1\\ufee2\\ufee3\\ufee4]", "\u0645"); // meem
        rules.put("[\\ufee5\\ufee6\\ufee7\\ufee8]", "\u0646"); // noon
        rules.put("[\\ufee9\\ufeea\\ufeeb\\ufeec]", "\u0647"); // heh
        rules.put("[\\ufeed\\ufeee]", "\u0648"); // waw
        rules.put("[\\ufeef\\ufef0]", "\u0649"); // alef maksura
        rules.put("[\\ufef1\\ufef2\\ufef3\\ufef4]", "\u064a"); // yeh
        rules.put("[\\ufef5\\ufef6]", "\u0644\u0622");  // ligature: lam and alef with madda above
        rules.put("[\\ufef7\\ufef8]", "\u0644\u0623");  // ligature: lam and alef with hamza above
        rules.put("[\\ufef9\\ufefa]", "\u0644\u0625"); // ligature: lam and alef with hamza below
        rules.put("[\\ufefb\\ufefc]", "\u0644\u0627"); // ligature: lam and alef

        return rules;

    }


    public static Map<String, String> getArabicIBMNormalizerMap() {

        Map<String, String> rules = new HashMap<>();

        try {
            rules.put("[\\u0622\\u0623\\u0625]", "\u0627"); // hamza normalization: maddah-n-alef, hamza-on-alef, hamza-under-alef mapped to bare alef

            rules.put("[\\u0649]", "\u064A");  // 'alif maqSuura mapped to yaa

            rules.put("[\\u064B\\u064C\\u064D\\u064E\\u064F\\u0650\\u0651\\u0652\\u0653\\u0670]", "");  //  fatHatayn, Dammatayn, kasratayn, fatHa, Damma, kasra, shaddah, sukuun, and dagger alef (delete)

            rules.put("\\u0640(?=\\s*\\S)", ""); // tatweel, delete except when trailing
            rules.put("(\\S)\\u0640", "$1"); // tatweel, delete if preceeded by non-white-space


            rules.put("[\\ufeff\\u00a0]", " "); // white space normalization

            // punctuation normalization

            rules.put("\\u060c", ","); // Arabic comma
            rules.put("\\u061b", ";"); // Arabic semicolon
            rules.put("\\u061f", "?"); // Arabic question mark
            rules.put("\\u066a", "%"); // Arabic percent sign
            rules.put("\\u066b", "."); // Arabic decimal separator
            rules.put("\\u066c", ","); // Arabic thousand separator (comma)
            rules.put("\\u066d", "*"); // Arabic asterisk
            rules.put("\\u06d4", "."); // Arabic full stop

            // Arabic/Arabic indic/eastern Arabic/ digits normalization

            rules.put("[\\u0660\\u06f0\\u0966]", "0");
            rules.put("[\\u0661\\u06f1\\u0967]", "1");
            rules.put("[\\u0662\\u06f2\\u0968]", "2");
            rules.put("[\\u0663\\u06f3\\u0969]", "3");
            rules.put("[\\u0664\\u06f4\\u096a]", "4");
            rules.put("[\\u0665\\u06f5\\u096b]", "5");
            rules.put("[\\u0666\\u06f6\\u096c]", "6");
            rules.put("[\\u0667\\u06f7\\u096d]", "7");
            rules.put("[\\u0668\\u06f8\\u096e]", "8");
            rules.put("[\\u0669\\u06f9\\u096f]", "9");

            // Arabic combining hamza above/below and dagger(superscript)  alef
            rules.put("[\\u0654\\u0655\\u0670]", "");

            // replace yaa followed by hamza with hamza on kursi (yaa)
            rules.put("\\u064A\\u0621", "\u0626");

            // Normalization Rules Suggested by Ralf Brown (CMU):


            rules.put("\\u2013", "-"); // EN-dash to ASCII hyphen
            rules.put("\\u2014", "--"); // EM-dash to double ASII hyphen

            // code point 0x91 - latin-1 left single quote
            // code point 0x92 - latin-1 right single quote
            // code point 0x2018 = left single quote; convert to ASCII single quote
            // code point 0x2019 = right single quote; convert to ASCII single quote

            rules.put("[\\u0091\\u0092\\u2018\\u2019]", "\'");

            // code point 0x93 - latin-1 left double quote
            // code point 0x94 - latin-1 right double quote
            // code points 0x201C/201D = left/right double quote -> ASCII double quote

            rules.put("[\\u0093\\u0094\\u201C\\u201D]", "\"");

        } catch (Exception e) {
//      log.info("Caught exception creating Arabic normalizer map: " + e.toString() );
        }

        return rules;
    }


    /**
     * This will normalize a Unicode String by applying all the normalization rules from the IBM normalization and
     * conversion from Presentation to Logical from.
     *
     * @param in The String to be normalized
     */
    public static String normalize0(String in) {

        Map<String, String> ruleMap = getArabicIBMNormalizerMap();   //Get the IBM Normalization rules

        ruleMap.putAll(presToLogicalMap());   //  Get the presentation to logical form rules

        Set<Map.Entry<String, String>> rules = ruleMap.entrySet();

        Iterator<Map.Entry<String, String>> ruleIter = rules.iterator();

        String out = in;

        //Iteratively apply each rule to the string.
        while (ruleIter.hasNext()) {
            Map.Entry<String, String> thisRule = ruleIter.next();
            out = out.replaceAll(thisRule.getKey(), thisRule.getValue());
        }

        return out;
    }


    public static void main(String[] args) throws IOException {

        int searchIndex[] = searchInStringWordByWord(source, sw, 0);
//        int searchIndex[] = searchInStringLetterByLetter(source, sw, 0);
        while (searchIndex[0] >= 0) {
            System.out.println("start index: " + searchIndex[0] + " - end index: " + searchIndex[1]);
            // Do something useful with this indexes, eg. highlighting
            searchIndex = searchInStringWordByWord(source, sw, searchIndex[1] + 1);
//            searchIndex = searchInStringLetterByLetter(source, sw, searchIndex[1] + 1);
        }
    }


    /**
     * This method will search through input string for search word, word by word
     * Note that it will search the input word by word so it is really fast but has some cons
     * so use it if you know the pros and cons
     * <p>
     * Pros: It is really really fast as hell :)
     * <p>
     * Cons: It search word by word, so you can not search for sw = "مه", actually you can BUT this method wont find it :)
     * You must search for whole word, number of word is not matter but they must be whole word
     * If you have input = "مِهدی؛ سُهرابی" and you search for sw = "مهدی" this method wont find any thing cause it
     * consider مِهدی؛ as one word and obviously it is different from your search word
     *
     * @param input The input string
     * @param sw    The search word
     * @param index The index that search will start from
     * @return array of 2 int contain start index and end index, or {-1,-1} in case it did not find the search word
     * result[0] -> firstIndex
     * result[1] -> lastIndex
     */
    public static int[] searchInStringWordByWord(String input, String sw, int index) {
        int[] result = new int[2];
        result[0] = -1;
        result[1] = -1;

        int counter = index;

        input = input.substring(index, input.length());

        final Collator instance = Collator.getInstance(new Locale("ar"));
        instance.setStrength(Collator.NO_DECOMPOSITION);

        String[] source = input.split(" ");
        String[] target = sw.split(" ");

        for (int i = 0; i < source.length; i++) {
            if (instance.compare(source[i], target[0]) == 0) {
                //first char is equal
                if (target.length > 1) {
                    // check other char
                    boolean exist = searchForCompanionWords(instance, source, target, i, 0);
                    if (exist) {
                        result[0] = counter;
                        result[1] = getLastIndex(source, i, target.length, counter);
                    }
                } else {
                    result[0] = counter;
                    result[1] = counter + source[i].length();
                    return result;
                }
            }

            counter += source[i].length() + 1; // +1 for omitted space
        }

        return result;
    }

    /**
     * If the search word is more than one word, this method comes in place and use a recursive method to checks for
     * equality, it advance one by one into source and target and check them and return true if all of them is equal
     * I know it is recursive BUT it will execute 2 or 3 times (depends on number of word in search word) so it is Not
     * big deal in performance
     *
     * @param collator    The java Collator that use for comparison
     * @param source      The source string the is space splitted
     * @param target      The search word that is space splitted
     * @param sourceMatch The index in source that match the `targetMatch` in search word
     * @param targetMatch The index in search word that match the `sourceMatch` in source
     * @return true if other words are equal and false otherwise
     */
    private static boolean searchForCompanionWords(Collator collator, String[] source, String[] target, int sourceMatch,
                                                   int targetMatch) {
        if (source.length <= sourceMatch + 1) return false;
        if (target.length <= targetMatch + 1) return false;

        if (collator.compare(source[sourceMatch + 1], target[targetMatch + 1]) == 0) {
            // The next char matches, is there any target word?
            if (target.length <= targetMatch + 2) {
                return true;
            } else {
                // There is more target word
                return searchForCompanionWords(collator, source, target, sourceMatch + 1, targetMatch + 1);
            }
        }

        return false;
    }

    /**
     * Get last index by lopping trough source
     *
     * @param source  The space splitted source
     * @param start   Start index in source
     * @param howMany Advance number
     * @param size    Size of strings in source before *start* index
     * @return The last index
     */
    private static int getLastIndex(String[] source, int start, int howMany, int size) {
        for (int i = start; i < start + howMany; i++) {
            size += source[i].length() + 1; // +1 for omitted space
        }

        return size - 1; // -1 for last extra space
    }

    /**
     * This method will search through input string for search word letter by letter
     * Note that it will search the input letter by letter so it is SLOW (i tries to optimized it and this is as far as
     * i can go). so use it if you know the pros and cons
     * <p>
     * Pros: it can search anything, a letter, a word, anything if you have input = "مِهدی سُهرابی" you can search for
     * sw = "مه" and it find it for you
     * <p>
     * Cons: Really slow and unusable for long search
     *
     * @param input The input string
     * @param sw    The search word
     * @param index The index that search will start from
     * @return Array of 2 int contain start index and end index, or {-1,-1} in case it did not find the search word
     * result[0] -> firstIndex
     * result[1] -> lastIndex
     */
    public static int[] searchInStringLetterByLetter(String input, String sw, int index) {
        int[] result = new int[2];
        int counter = 0;
        String temp;

        for (int i = index; i < input.length(); i++) {
            temp = removeDiacritic(input.substring(i, i + 1));
            if (temp.equals("")) continue; // if it is empty it means the char is diacritic

            if (sw.length() - 1 == counter) {
                //the char found
                result[1] = i + 1; // This is the last index
                return result;
            }

            if (temp.equals(sw.substring(counter, counter + 1))) {
                // the first char is equal, search for other
                if (counter == 0) {
                    result[0] = i;
                }
                ++counter;
            } else {
                // reset char comparison
                counter = 0;
            }
        }

        result[0] = -1;
        result[1] = -1;
        return result;
    }

    /**
     * Remove any diacritic from input string
     * less diacritics means faster search
     *
     * @param input The input string
     * @return Diacritic free string
     */
    public static String removeDiacritic(String input) {
        input = input.replaceAll("\u0650", "");
        input = input.replaceAll("\u0651", "");
        input = input.replaceAll("\u0652", "");
        input = input.replaceAll("\u064E", "");
        input = input.replaceAll("\u064F", "");
        input = input.replaceAll("\u064D", "");
        input = input.replaceAll("\u064B", "");
        return input;
    }


    public static String normalize(String input) {

        //Remove honorific sign
        input = input.replaceAll("\u0610", "")//ARABIC SIGN SALLALLAHOU ALAYHE WA SALLAM
                .replaceAll("\u0611", "")//ARABIC SIGN ALAYHE ASSALLAM
                .replaceAll("\u0612", "")//ARABIC SIGN RAHMATULLAH ALAYHE
                .replaceAll("\u0613", "")//ARABIC SIGN RADI ALLAHOU ANHU
                .replaceAll("\u0614", "")//ARABIC SIGN TAKHALLUS

                //Remove koranic anotation
                .replaceAll("\u0615", "")//ARABIC SMALL HIGH TAH
                .replaceAll("\u0616", "")//ARABIC SMALL HIGH LIGATURE ALEF WITH LAM WITH YEH
                .replaceAll("\u0617", "")//ARABIC SMALL HIGH ZAIN
                .replaceAll("\u0618", "")//ARABIC SMALL FATHA
                .replaceAll("\u0619", "")//ARABIC SMALL DAMMA
                .replaceAll("\u061A", "")//ARABIC SMALL KASRA
                .replaceAll("\u06D6", "")//ARABIC SMALL HIGH LIGATURE SAD WITH LAM WITH ALEF MAKSURA
                .replaceAll("\u06D7", "")//ARABIC SMALL HIGH LIGATURE QAF WITH LAM WITH ALEF MAKSURA
                .replaceAll("\u06D8", "")//ARABIC SMALL HIGH MEEM INITIAL FORM
                .replaceAll("\u06D9", "")//ARABIC SMALL HIGH LAM ALEF
                .replaceAll("\u06DA", "")//ARABIC SMALL HIGH JEEM
                .replaceAll("\u06DB", "")//ARABIC SMALL HIGH THREE DOTS
                .replaceAll("\u06DC", "")//ARABIC SMALL HIGH SEEN
                .replaceAll("\u06DD", "")//ARABIC END OF AYAH
                .replaceAll("\u06DE", "")//ARABIC START OF RUB EL HIZB
                .replaceAll("\u06DF", "")//ARABIC SMALL HIGH ROUNDED ZERO
                .replaceAll("\u06E0", "")//ARABIC SMALL HIGH UPRIGHT RECTANGULAR ZERO
                .replaceAll("\u06E1", "")//ARABIC SMALL HIGH DOTLESS HEAD OF KHAH
                .replaceAll("\u06E2", "")//ARABIC SMALL HIGH MEEM ISOLATED FORM
                .replaceAll("\u06E3", "")//ARABIC SMALL LOW SEEN
                .replaceAll("\u06E4", "")//ARABIC SMALL HIGH MADDA
                .replaceAll("\u06E5", "")//ARABIC SMALL WAW
                .replaceAll("\u06E6", "")//ARABIC SMALL YEH
                .replaceAll("\u06E7", "")//ARABIC SMALL HIGH YEH
                .replaceAll("\u06E8", "")//ARABIC SMALL HIGH NOON
                .replaceAll("\u06E9", "")//ARABIC PLACE OF SAJDAH
                .replaceAll("\u06EA", "")//ARABIC EMPTY CENTRE LOW STOP
                .replaceAll("\u06EB", "")//ARABIC EMPTY CENTRE HIGH STOP
                .replaceAll("\u06EC", "")//ARABIC ROUNDED HIGH STOP WITH FILLED CENTRE
                .replaceAll("\u06ED", "")//ARABIC SMALL LOW MEEM

                //Remove tatweel
                .replaceAll("\u0640", "")

                //Remove tashkeel
                .replaceAll("\u064B", "")//ARABIC FATHATAN
                .replaceAll("\u064C", "")//ARABIC DAMMATAN
                .replaceAll("\u064D", "")//ARABIC KASRATAN
                .replaceAll("\u064E", "")//ARABIC FATHA
                .replaceAll("\u064F", "")//ARABIC DAMMA
                .replaceAll("\u0650", "")//ARABIC KASRA
                .replaceAll("\u0651", "")//ARABIC SHADDA
                .replaceAll("\u0652", "")//ARABIC SUKUN
                .replaceAll("\u0653", "")//ARABIC MADDAH ABOVE
                .replaceAll("\u0654", "")//ARABIC HAMZA ABOVE
                .replaceAll("\u0655", "")//ARABIC HAMZA BELOW
                .replaceAll("\u0656", "")//ARABIC SUBSCRIPT ALEF
                .replaceAll("\u0657", "")//ARABIC INVERTED DAMMA
                .replaceAll("\u0658", "")//ARABIC MARK NOON GHUNNA
                .replaceAll("\u0659", "")//ARABIC ZWARAKAY
                .replaceAll("\u065A", "")//ARABIC VOWEL SIGN SMALL V ABOVE
                .replaceAll("\u065B", "")//ARABIC VOWEL SIGN INVERTED SMALL V ABOVE
                .replaceAll("\u065C", "")//ARABIC VOWEL SIGN DOT BELOW
                .replaceAll("\u065D", "")//ARABIC REVERSED DAMMA
                .replaceAll("\u065E", "")//ARABIC FATHA WITH TWO DOTS
                .replaceAll("\u065F", "")//ARABIC WAVY HAMZA BELOW
                .replaceAll("\u0670", "")//ARABIC LETTER SUPERSCRIPT ALEF


                .replaceAll("[إأآا]", "ا")
                .replaceAll("ى", "ي")
                .replaceAll("ؤ", "ء")
                .replaceAll("ئ", "ء")
                .replaceAll("ة", "ه")
                .replaceAll("گ", "ك");


//                .replaceAll("(", "")
//                .replaceAll(")", "");
        return input;
    }

    public static HashMap<String, String> createSqliteRemoveString(String field) {

        char[] chars = field.toCharArray();
        HashMap<String, String> map = new HashMap();

        for (char c : chars) {
            map.put(String.valueOf(c), "");
        }

        return map;
    }

    public static String createSqliteReplace(String field, Map<String, String> ruleMap) {
        Set<Map.Entry<String, String>> rules = ruleMap.entrySet();

        Iterator<Map.Entry<String, String>> iterator = rules.iterator();

        String s = field;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            s = "replace(" + s + ",'" + entry.getKey() + "','" + entry.getValue() + "')";
        }

        return s;
    }
}