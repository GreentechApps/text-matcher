import java.util.HashMap;

class DiacriticsResearch {

    public static void main(String[] args) {

        String dua = "اذهب الباس رب الناس واشف انت الشافي لا شفاء الا شفاءك شفاء لا يغادر سقما";

        String s = DiacriticInsensitiveSearch.removeDiacritic(dua);
        String s2 = DiacriticInsensitiveSearch.normalize(dua);
        String s3 = dua.replaceAll("(ّ)?(َ)?(ً)?(ُ)?(ٌ)?(ِ)?(ٍ)?(~)?(ْ)?", "");
        String s4 = ArabicUtils.normalize(dua);

        System.out.println(dua + "\n" + s + "\n" + s2 + "\n" + s3 + "\n" + s4);

        HashMap<String, String> map = DiacriticInsensitiveSearch.createSqliteRemoveString("\"،.?,()_-{}ـ*");

//       map.put("ا", "ا");
//       map.put("آ", "ا");
//       map.put("أ", "ا");
//       map.put("إ", "ا");
//       map.put("ى", "ي");
//       map.put("ؤ", "ء");
//       map.put("ئ", "ء");
//       map.put("ة", "ه");
//       map.put("گ", "ك");

        String arabic = DiacriticInsensitiveSearch.createSqliteReplace("text_ar_diacless", map);
        System.out.println(arabic);
    }

}