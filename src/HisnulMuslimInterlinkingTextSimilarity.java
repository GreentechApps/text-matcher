import stringsimilarity.Cosine;
import stringsimilarity.Levenshtein;
import stringsimilarity.SorensenDice;

import java.sql.*;
import java.util.Map;

public class HisnulMuslimInterlinkingTextSimilarity {


    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Load driver success");

            Connection connectionHisnul = DriverManager.getConnection("jdbc:sqlite:D:\\hisnulbd.db");
            Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

            Statement stmtHadith = connectionHadith.createStatement();

            Statement stmtHisnul = connectionHisnul.createStatement();
            stmtHisnul.execute("update duadetails set app_reference ='' where app_reference is null ");

            stmtHisnul.execute("UPDATE duadetails SET arabic = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(arabic, 'ِ', ''), 'َ' , ''), 'ّ' , ''), 'ً' , ''), 'ٍ' , '') , 'ٌ' , '') , 'ْ' , '') , 'ٓ' , '') , 'ُ' , '')");
//
            //normalise text
            stmtHisnul.execute("UPDATE duadetails SET arabic = replace(replace(replace(replace(replace(replace(replace(replace(replace(arabic,'آ','ا'),'أ','ا'),'ؤ','ء'),'إ','ا'),'ئ','ء'),'ا','ا'),'ى','ي'),'ة','ه'),'گ','ك')");

            //remove puntuations
            stmtHisnul.execute("UPDATE duadetails SET arabic = replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(arabic,'ـ',''),'\"',''),'(',''),')',''),'*',''),'،',''),',',''),'-',''),'.',''),'{',''),'}',''),'?',''),'_','')");

            ResultSet rs = stmtHisnul.executeQuery("select _id, arabic, app_reference from duadetails");
//            SorensenDice sd = new SorensenDice(2);
            Cosine cos = new Cosine(2);
            String selectQuery = "select * from hadiths";

            PreparedStatement statement = connectionHadith.prepareStatement(selectQuery);

            while (rs.next()) {
                long t = System.currentTimeMillis();
                String arabic = DiacriticInsensitiveSearch.normalize(rs.getString(2).replace("(", "").replace(")", ""));

                if (arabic.isEmpty() || arabic.length() < 10) {
                    continue;
                }

                Map<String, Integer> arabicProfile = cos.getProfile(arabic);

                String id = rs.getString(1);
                String oldAppReference = rs.getString(3);

                StringBuilder app_reference = new StringBuilder();
                ResultSet resultSet = statement.executeQuery();
                int textArDiaclessColumn = resultSet.findColumn("text_ar_diacless");

                while (resultSet.next()) {
                    String text_ar_diacless = resultSet.getString(textArDiaclessColumn);

                    //if length is too much far that means they are really not similar
                    if (Math.abs(arabic.length() - text_ar_diacless.length()) > 100) {
                        continue;
                    }

                    double similarity = cos.similarity(arabicProfile, cos.getProfile(text_ar_diacless));
                    if (similarity > 0.9) {
                        String reference = resultSet.getString(1) + ":" + resultSet.getString(2) + ":" + resultSet.getString(4);

                        if (oldAppReference.contains("9:" + reference) ||oldAppReference.contains("8:" + reference)||oldAppReference.contains("7:" + reference)) {
                        continue;
                        }

                        app_reference.append(",").append("7:" + reference);

                        System.out.println("Found text similariry of " + similarity + " at " + reference + " for " + id);
                    }
                }
                resultSet.close();

                if (app_reference.length() > 0) {
                    oldAppReference = oldAppReference + app_reference;
                    if (oldAppReference.startsWith(",")) {
                        oldAppReference = oldAppReference.replaceFirst(",", "");
                    }

                    String query = "update duadetails set app_reference = '" + oldAppReference + "' where _id=" + id;
                    PreparedStatement preparedStatement = connectionHisnul.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    System.out.println("Updated reference at " + id + ": " + app_reference);
                }

                System.out.println("Done " + id + " in " + (System.currentTimeMillis() - t) + " ms");
            }

            rs.close();
            connectionHadith.close();
            connectionHisnul.close();
            System.out.println("Insert success");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
