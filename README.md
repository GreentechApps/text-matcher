# Text similarity algorithms for matching Islamic Hadith Documents for Hadith Interlinking.
Scripts written to facillitate the process of greentechapps to interlink hadiths present in different apps. 

##Technology Stack

I used java only because I was excellent in java. Thus I never needed to use python even though I know python as well. We already had our database in sqlite we just used the jdbc driver for sqlite. Text similarity algorithms in java can be found here.
Our goal is find and match each hadith with all others to see which of them are very much similar to one another.  We had around 42000 Hadiths based on the data of sunnah.com.

### The Text Matching process
### Tokenise the arabic sentense

I had both arabic and english texts for the hadiths. I started with arabic text, started removing all arabic diacritcs, qoutation marks, full stops, commas. Tokenising arabic is a bit more difficult than english as some letters such as hamza, ya, wow have multiple variations. So we used normalisation as well.
        
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

                //normalise multiple variations into their most basic forms
                .replaceAll("[إأآا]", "ا")
                .replaceAll("ى", "ي")
                .replaceAll("ؤ", "ء")
                .replaceAll("ئ", "ء")
                .replaceAll("ة", "ه")
                .replaceAll("گ", "ك");


                 //.replaceAll("(", "")
                //.replaceAll(")", "");
        return input;
    }
    
### 2. *Using Sqlite match*
After tokenisation and normalisation is done, we initially testing uses sqlite's full-text-search mechanism to see if there were any matches using this system. We found many matches using this method however had to limit the match for longer texts only as it was very common that a small sentence matched in many hadiths and in that case they really were not similar. 
               
               "select CollectionID, BookID, HadithID from hadith where text_ar_diacless match ?"

### 3. *Using text similarity algorithms* 
With a readymade test sample we initially found out which were giving most accurate similarity results. The results were based on a decimal value between 0 and 1 where 1 means the texts are absolutely similar. Based on that Cosine Similarity and Sorenson-dice coefficient gave the best results on primary interpretation.
Cosine similarity

It calculates similarity by measuring the cosine of angle between two vectors and is computed as V1 . V2 / (|V1| * |V2|)
Sorensen-Dice coefficient

The input strings are first converted into sets of n-grams (sequences of n characters, also called k-shingles). Each input string is simply a set of n-grams. The similarity is computed as 2 * |V1 inter V2| / (|V1| + |V2|).

We choose Cosine Similarity initially as it was faster than Sorenson-Dice. However upon applying it over all the hadiths, we found out there were a lot of false positives. Even when the cosine similarity was a 90% match it wasnt actually correct, especially for larger texts. We shifted to Sorenson dice and were very satisfied with it even when the percentage was 75% there were matches that were accurate. However for longer texts it was a problem, so we had to divide the n-grams into 2 and 3 sets and then check similarity for both cases to find out if a text is really similar. 

So finally this is the code which matches if a hadith is actually similar with the hadith that we are checking

        double similarityK2 = sd.similarity(currentHadith.getProfileK2(), checkForMatchHadith.getProfileK2());
        double similarityK3 = sd3.similarity(currentHadith.getProfileK3(), checkForMatchHadith.getProfileK3());
        double dif = similarityK2 - similarityK3;
        if (similarityK2 > 0.75 && dif < 0.27) {//ensure similarity is around 75% and the dif is there to ensure similarity is more accurate in larger texts
         related_en.append(",").append("9:" + checkForMatchHadith.getReference());

         System.out.println("Found similariry of " + similarityK2 + " at " + checkForMatchHadith.getReference() + " for " + currentHadith.getRowid());
      }
      
This does make the process slow. It took over 4 hours running synchronously to match 42000 hadiths againest each other.
