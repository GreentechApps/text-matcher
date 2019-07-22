import stringsimilarity.*;

import java.util.Locale;

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
//        String test2 = "\u200F اذهب الباس رب الناس اشفه انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F";
//        String test3 = "وحدثناه ابو بكر بن ابي شيبه وزهير بن حرب قالا حدثنا جرير عن منصور عن ابي الضحي عن مسروق عن عاءشه قالت كان رسول الله صلي الله عليه وسلم اذا اتي المريض يدعو له قال \u200F\u200F اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F\u200F \u200F\u200F وفي روايه ابي بكر فدعا له وقال \u200F\u200F وانت الشافي \u200F\u200F \u200F\u200F";
//        String test4 = "\u200F اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F";
//        String test5 = "\u200F اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما \u200F";


//dua 31
        String dua = "الحمد لله الذي كساني هذا الثوب ورزقنيه من غير حول مني ولا قوه";

        String test1 = "وحدثنا محمد بن المثني حدثنا يزيد اخبرنا الجريري بهذا الاسناد نحوه غير انه قال وكان اهل مكه قوم حسد \u200F\u200F ولم يقل يحسدونه \u200F\u200F";
        String test2 = "وحدثنا قتيبه بن سعيد حدثنا حاتم بهذا الاسناد \u200F\u200F غير انه قال في كلتيهما سبع غزوات \u200F\u200F";
        String test3 = "وحدثني زهير بن حرب حدثنا جرير ح وحدثنا قتيبه حدثنا عبد العزيز  يعني الدراوردي  كلاهما عن سهيل بهذا الاسناد \u200F\u200F";
        String test4 = "وحدثنيه محمد بن عبد الله بن قهزاذ حدثني عبد الله بن عثمان عن عبد الله بن المبارك عن يونس عن الزهري بهذا الاسناد مثله \u200F\u200F";
        String test5 = "قال ابو اسحاق ابراهيم بن محمد حدثنا محمد بن يحيي حدثنا ابن ابي مريم حدثنا ابو غسان حدثنا زيد بن اسلم عن عطاء بن يسار \u200F\u200F وذكر الحديث نحوه \u200F\u200F";

        // Sorensen-Dice
        // =============
        System.out.println("\nSorensen-Dice");
        SorensenDice sd = new SorensenDice(2);

        // AB BC CD DE DF FG
        // 1  1  1  1  0  0
        // 1  1  1  0  1  1
        // => 2 x 3 / (4 + 5) = 6/9 = 0.6666
        System.out.println(sd.similarity(dua, test1));
        System.out.println(sd.similarity(dua, test2));
        System.out.println(sd.similarity(dua, test3));
        System.out.println(sd.similarity(dua, test4));
        System.out.println(sd.similarity(dua, test5));

        // Cosine
        // ======
        System.out.println("\nCosine");
        Cosine cos = new Cosine(3);

        // ABC BCE
        // 1  0
        // 1  1
        // angle = 45°
        // => similarity = .71
        System.out.println(cos.similarity(dua, test1));
        System.out.println(cos.similarity(dua, test2));
        System.out.println(cos.similarity(dua, test3));
        System.out.println(cos.similarity(dua, test4));
        System.out.println(cos.similarity(dua, test5));

        System.out.println("\n");
        cos = new Cosine(2);
        // AB BA
        // 2  1
        // 1  1
        // similarity = .95
        System.out.println(cos.similarity(dua, test1));
        System.out.println(cos.similarity(dua, test2));
        System.out.println(cos.similarity(dua, test3));
        System.out.println(cos.similarity(dua, test4));
        System.out.println(cos.similarity(dua, test5));


        // Jaccard index
        // =============
        System.out.println("\nJaccard");
        Jaccard j2 = new Jaccard(2);
        // AB BC CD DE DF
        // 1  1  1  1  0
        // 1  1  1  0  1
        // => 3 / 5 = 0.6
        System.out.println(j2.similarity(dua, test1));
        System.out.println(j2.similarity(dua, test2));
        System.out.println(j2.similarity(dua, test3));
        System.out.println(j2.similarity(dua, test4));
        System.out.println(j2.similarity(dua, test5));

        // Jaro-Winkler
        // ============
        System.out.println("\nJaro-Winkler");
        JaroWinkler jw = new JaroWinkler();

        // substitution of s and t : 0.9740740656852722
        System.out.println(jw.similarity(dua, test1));
        System.out.println(jw.similarity(dua, test2));
        System.out.println(jw.similarity(dua, test3));
        System.out.println(jw.similarity(dua, test4));
        System.out.println(jw.similarity(dua, test5));


        // Levenshtein
        // ===========
        System.out.println("\nLevenshtein");
        Levenshtein levenshtein = new Levenshtein();
        System.out.println(levenshtein.distance(dua, test1));
        System.out.println(levenshtein.distance(dua, test2));
        System.out.println(levenshtein.distance(dua, test3));
        System.out.println(levenshtein.distance(dua, test4));
        System.out.println(levenshtein.distance(dua, test5));

        // Damerau
        // =======
        System.out.println("\nDamerau");
        Damerau damerau = new Damerau();

        // 1 substitution
        System.out.println(damerau.distance(dua, test1));
        System.out.println(damerau.distance(dua, test2));
        System.out.println(damerau.distance(dua, test3));
        System.out.println(damerau.distance(dua, test4));
        System.out.println(damerau.distance(dua, test5));

        // Optimal String Alignment
        // =======
        System.out.println("\nOptimal String Alignment");
        OptimalStringAlignment osa = new OptimalStringAlignment();

        //Will produce 3.0
        System.out.println(osa.distance(dua, test1));
        System.out.println(osa.distance(dua, test2));
        System.out.println(osa.distance(dua, test3));
        System.out.println(osa.distance(dua, test4));
        System.out.println(osa.distance(dua, test5));


        // Longest Common Subsequence
        // ==========================
        System.out.println("\nLongest Common Subsequence");
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();

        // Will produce 4.0
        System.out.println(lcs.distance(dua, test1));
        System.out.println(lcs.distance(dua, test2));
        System.out.println(lcs.distance(dua, test3));
        System.out.println(lcs.distance(dua, test4));
        System.out.println(lcs.distance(dua, test5));

        // Normalized Levenshtein
        // ======================
        System.out.println("\nNormalized Levenshtein");
        NormalizedLevenshtein l = new NormalizedLevenshtein();

        System.out.println(l.distance(dua, test1));
        System.out.println(l.distance(dua, test2));
        System.out.println(l.distance(dua, test3));
        System.out.println(l.distance(dua, test4));
        System.out.println(l.distance(dua, test5));

        // QGram
        // =====
        System.out.println("\nQGram");
        QGram dig = new QGram(2);

        // AB BC CD CE
        // 1  1  1  0
        // 1  1  0  1
        // Total: 2
        System.out.println(dig.distance(dua, test1));
        System.out.println(dig.distance(dua, test2));
        System.out.println(dig.distance(dua, test3));
        System.out.println(dig.distance(dua, test4));
        System.out.println(dig.distance(dua, test5));


        System.out.println("\nFuzzy Score");
        FuzzyScore score = new FuzzyScore(Locale.getDefault());

        System.out.println(score.fuzzyScore(dua, test1));
        System.out.println(score.fuzzyScore(dua, test2));
        System.out.println(score.fuzzyScore(dua, test3));
        System.out.println(score.fuzzyScore(dua, test4));
        System.out.println(score.fuzzyScore(dua, test5));
    }

}