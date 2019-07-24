import javadatatext.*;
import javafx.scene.paint.Stop;
import stringsimilarity.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

class StringSimilarityTest {

    public static void main(String[] args) {

//        String dua = "الحمد لله الذي أحيانا بعد ما أماتنا، وإليه النشور";
//
//        String test1 = "حدثنا أبو نعيم، حدثنا سفيان، عن عبد الملك بن عمير، عن ربعي بن حراش، عن حذيفة، قال كان النبي صلى الله عليه وسلم إذا أراد أن ينام قال \u200F\u200F باسمك اللهم أموت وأحيا \u200F\"\u200F\u200F.\u200F وإذا استيقظ من منامه قال \u200F\"\u200F الحمد لله الذي أحيانا بعد ما أماتنا، وإليه النشور \u200F\"\u200F\u200F.\u200F\"";
//
//        String test2 = "حدثني موسى بن إسماعيل، حدثنا أبو عوانة، عن عبد الملك، عن ربعي، عن حذيفة ـ رضى الله عنه قال كان النبي صلى الله عليه وسلم إذا أخذ مضجعه من الليل وضع يده تحت خده ثم يقول \u200F\u200F اللهم باسمك أموت وأحيا \u200F\"\u200F\u200F.\u200F وإذا استيقظ قال \u200F\"\u200F الحمد لله الذي أحيانا بعد ما أماتنا وإليه النشور \u200F\"\u200F\u200F.\u200F\"";
//
//        String test4 = "\u200F سيد الاستغفار اللهم أنت ربي لا إله إلا أنت، خلقتني وأنا عبدك، وأنا على عهدك ووعدك ما استطعت، أبوء لك بنعمتك، وأبوء لك بذنبي، فاغفر لي، فإنه لا يغفر الذنوب إلا أنت، أعوذ بك من شر ما صنعت\u200F.\u200F إذا قال حين يمسي فمات دخل الجنة ـ أو كان من أهل الجنة ـ وإذا قال حين يصبح فمات من يومه \u200F\"";
//
//        String test3 = "حدثنا عبيد الله بن معاذ، حدثنا أبي، حدثنا شعبة، عن عبد الله بن أبي السفر، عن أبي بكر بن أبي موسى، عن البراء، أن النبي صلى الله عليه وسلم كان إذا أخذ مضجعه قال \u200F\u200F اللهم باسمك أحيا وباسمك أموت \u200F\"\u200F \u200F.\u200F وإذا استيقظ قال \u200F\"\u200F الحمد لله الذي أحيانا بعد ما أماتنا وإليه النشور \u200F\"\u200F \u200F.\u200F\"";
//
//
//        String test5 = "أخبرنا العباس بن محمد، قال أنبأنا أبو نوح، قال حدثنا جرير بن حازم، عن عبد الملك بن عمير، عن إياد بن لقيط، عن أبي رمثة، قال خرج علينا رسول الله صلى الله عليه وسلم وعليه ثوبان أخضران \u200F.\u200F";


        //dua 328 id
//        String dua = "اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما";
//
//        String test1 = "حدثنا زهير بن حرب واسحاق بن ابراهيم قال اسحاق اخبرنا وقال زهير  واللفظ له  حدثنا جرير عن الاعمش عن ابي الضحي عن مسروق عن عاءشه قالت كان رسول الله صلي الله عليه وسلم اذا اشتكي منا انسان مسحه بيمينه ثم قال \u200F\u200F اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F\u200F \u200F\u200F فلما مرض رسول الله صلي الله عليه وسلم وثقل اخذت بيده لاصنع به نحو ما كان يصنع فانتزع يده من يدي ثم قال \u200F\u200F اللهم اغفر لي واجعلني مع الرفيق الاعلي \u200F\u200F \u200F\u200F قالت فذهبت انظر فاذا هو قد قضي \u200F\u200F";
//        String test = "\u200F اذهب الباس رب الناس اشفه انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F";
//        String test3 = "وحدثناه ابو بكر بن ابي شيبه وزهير بن حرب قالا حدثنا جرير عن منصور عن ابي الضحي عن مسروق عن عاءشه قالت كان رسول الله صلي الله عليه وسلم اذا اتي المريض يدعو له قال \u200F\u200F اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F\u200F \u200F\u200F وفي روايه ابي بكر فدعا له وقال \u200F\u200F وانت الشافي \u200F\u200F \u200F\u200F";
//        String test4 = "\u200F اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F";
//        String test5 = "\u200F اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F";


//dua 31
//        String dua = "الحمد لله الذي كساني هذا الثوب ورزقنيه من غير حول مني ولا قوه";
//
//        String test = "وحدثنا محمد بن المثني حدثنا يزيد اخبرنا الجريري بهذا الاسناد نحوه غير انه قال وكان اهل مكه قوم حسد \u200F\u200F ولم يقل يحسدونه \u200F\u200F";
//        String test1 = "وحدثنا قتيبه بن سعيد حدثنا حاتم بهذا الاسناد \u200F\u200F غير انه قال في كلتيهما سبع غزوات \u200F\u200F";
//        String test3 = "وحدثني زهير بن حرب حدثنا جرير ح وحدثنا قتيبه حدثنا عبد العزيز  يعني الدراوردي  كلاهما عن سهيل بهذا الاسناد \u200F\u200F";
//        String test4 = "وحدثنيه محمد بن عبد الله بن قهزاذ حدثني عبد الله بن عثمان عن عبد الله بن المبارك عن يونس عن الزهري بهذا الاسناد مثله \u200F\u200F";
//        String test5 = "قال ابو اسحاق ابراهيم بن محمد حدثنا محمد بن يحيي حدثنا ابن ابي مريم حدثنا ابو غسان حدثنا زيد بن اسلم عن عطاء بن يسار \u200F\u200F وذكر الحديث نحوه \u200F\u200F";

        //31
        String dua =
        "I heard Allah's Messenger (ﷺ) saying, \"The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.";

        String test1 = "Allah's Messenger (ﷺ) said, \"The reward of deeds depends upon the intention and every person will get the reward according to what he has intended. So whoever emigrated for Allah and His Apostle, then his emigration was for Allah and His Apostle. And whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.";
        String test2 = "The Prophet (ﷺ) said, 'O people! The reward of deeds depends upon the intentions, and every person will get the reward according to what he has intended. So, whoever emigrated for Allah and His Apostle, then his emigration was for Allah and His Apostle, and whoever emigrated to take worldly benefit or for a woman to marry, then his emigration was for what he emigrated for.\"";

        String test3 = "وحدثني زهير بن حرب حدثنا جرير ح وحدثنا قتيبه حدثنا عبد العزيز  يعني الدراوردي  كلاهما عن سهيل بهذا الاسناد \u200F\u200F";
        String test4 = "وحدثنيه محمد بن عبد الله بن قهزاذ حدثني عبد الله بن عثمان عن عبد الله بن المبارك عن يونس عن الزهري بهذا الاسناد مثله \u200F\u200F";
        String test5 = "قال ابو اسحاق ابراهيم بن محمد حدثنا محمد بن يحيي حدثنا ابن ابي مريم حدثنا ابو غسان حدثنا زيد بن اسلم عن عطاء بن يسار \u200F\u200F وذكر الحديث نحوه \u200F\u200F";

        ArrayList<String> strings = new ArrayList<>();
        System.out.println(dua + "\n" + test1 + "\n" + test2);
        strings.add(test1);
        strings.add(test2);
        strings.add(test3);
        strings.add(test4);
        strings.add(test5);

        textSimilarityTest(dua, strings);


        TextFilter stemmer = new PorterStemmer();
        List<String> words = Arrays.asList(
                "caresses",
                "ponies",
                "ties",
                "caress",
                "cats",
                "feed",
                "agreed",
                "disabled",
                "matting",
                "mating",
                "meeting",
                "milling",
                "messing",
                "meetings"
        );

        List<String> result = stemmer.filter(words);
        for (int i = 0; i < words.size(); ++i)
        {
            System.out.println(String.format("%s -> %s", words.get(i), result.get(i)));
        }

        StopWordRemoval filter = new StopWordRemoval();

        filter.setRemoveNumbers(false);
        filter.setRemoveXmlTag(false);

        List<String> before = BasicTokenizer.doTokenize(dua);
        List<String> after = filter.filter(before);

        TextFilter filter1 = new PunctuationFilter();

        List<String> after1 = filter1.filter(before);

        System.out.println(dua);
        System.out.println(before);
        System.out.println(after);
        System.out.println(after1);
    }

    private static void textSimilarityTest(String dua, ArrayList<String> strings) {
        // Sorensen-Dice
        // =============
        System.out.println("\nSorensen-Dice");
        SorensenDice sd = new SorensenDice(2);

        // AB BC CD DE DF FG
        // 1  1  1  1  0  0
        // 1  1  1  0  1  1
        // => 2 x 3 / (4 + 5) = 6/9 = 0.6666
        for (String test : strings) {
            System.out.println(sd.similarity(dua, test));
        }

        // Cosine
        // ======
        System.out.println("\nCosine");
        Cosine cos = new Cosine(3);

        // ABC BCE
        // 1  0
        // 1  1
        // angle = 45°
        // => similarity = .71
        for (String test : strings) {
            System.out.println(cos.similarity(dua, test));
        }

        System.out.println("\n");
        cos = new Cosine(2);
        // AB BA
        // 2  1
        // 1  1
        // similarity = .95
        for (String test : strings) {
            System.out.println(cos.similarity(dua, test));
        }

        // Jaccard index
        // =============
        System.out.println("\nJaccard");
        Jaccard j2 = new Jaccard(2);
        // AB BC CD DE DF
        // 1  1  1  1  0
        // 1  1  1  0  1
        // => 3 / 5 = 0.6
        for (String test : strings) {
            System.out.println(j2.similarity(dua, test));
        }

        // Jaro-Winkler
        // ============
        System.out.println("\nJaro-Winkler");
        JaroWinkler jw = new JaroWinkler();

        // substitution of s and t : 0.9740740656852722
        for (String test : strings) {
            System.out.println(jw.similarity(dua, test));
        }

        // Levenshtein
        // ===========
        System.out.println("\nLevenshtein");
        Levenshtein levenshtein = new Levenshtein();
        for (String test : strings) {
            System.out.println(levenshtein.distance(dua, test));
        }

        // Damerau
        // =======
        System.out.println("\nDamerau");
        Damerau damerau = new Damerau();

        // 1 substitution
        for (String test : strings) {
            System.out.println(damerau.distance(dua, test));
        }

        // Optimal String Alignment
        // =======
        System.out.println("\nOptimal String Alignment");
        OptimalStringAlignment osa = new OptimalStringAlignment();

        //Will produce 3.0
        for (String test : strings) {
            System.out.println(osa.distance(dua, test));
        }


        // Longest Common Subsequence
        // ==========================
        System.out.println("\nLongest Common Subsequence");
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();

        // Will produce 4.0
        for (String test : strings) {
            System.out.println(lcs.distance(dua, test));
        }

        // Normalized Levenshtein
        // ======================
        System.out.println("\nNormalized Levenshtein");
        NormalizedLevenshtein l = new NormalizedLevenshtein();
        for (String test : strings) {
            System.out.println(l.distance(dua, test));
        }

        // QGram
        // =====
        System.out.println("\nQGram");
        QGram dig = new QGram(2);

        // AB BC CD CE
        // 1  1  1  0
        // 1  1  0  1
        // Total: 2
        for (String test : strings) {
            System.out.println(dig.distance(dua, test));
        }


        System.out.println("\nFuzzy Score");
        FuzzyScore score = new FuzzyScore(Locale.getDefault());

        for (String test : strings) {
            System.out.println(score.fuzzyScore(dua, test));
        }
    }

}