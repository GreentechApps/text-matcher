import stringsimilarity.Cosine;

import java.sql.*;
import java.util.Map;

public class HisnulMuslimInterlinking {


    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Load driver success");

            matchUsingSqliteMatch();
//            matchUsingDiceCoef();

            System.out.println("Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void matchUsingSqliteMatch() throws SQLException {
        Connection connectionHisnul = DriverManager.getConnection("jdbc:sqlite:D:\\hisnulbd.db");
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

        Statement stmtHisnul = connectionHisnul.createStatement();
        Statement stmtHadith = connectionHadith.createStatement();

        stmtHisnul.execute("update duadetails set app_reference ='' where app_reference is null ");

        stmtHisnul.execute("UPDATE duadetails SET arabic = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(arabic, 'ِ', ''), 'َ' , ''), 'ّ' , ''), 'ً' , ''), 'ٍ' , '') , 'ٌ' , '') , 'ْ' , '') , 'ٓ' , '') , 'ُ' , '')");
//
        //normalise text
        stmtHisnul.execute("UPDATE duadetails SET arabic = replace(replace(replace(replace(replace(replace(replace(replace(replace(arabic,'آ','ا'),'أ','ا'),'ؤ','ء'),'إ','ا'),'ئ','ء'),'ا','ا'),'ى','ي'),'ة','ه'),'گ','ك')");

        //remove puntuations
        stmtHisnul.execute("UPDATE duadetails SET arabic = replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(arabic,'ـ',''),'\"',''),'(',''),')',''),'*',''),'،',''),',',''),'-',''),'.',''),'{',''),'}',''),'?',''),'_','')");

        ResultSet rs = stmtHisnul.executeQuery("select _id, arabic, app_reference from duadetails");

        while (rs.next()) {
            String id = rs.getString(1);
            String arabic = ArabicUtils.normalize(rs.getString(2).replace("(", "").replace(")", ""));

            if (arabic.isEmpty() || arabic.length() < 15) {
                continue;
            }

            String oldAppReference = rs.getString(3);
            StringBuilder app_reference = new StringBuilder();
            String selectQuery = "select CollectionID, BookID, HadithID from hadiths where text_ar_diacless match '" + arabic + "'";

            ResultSet rsH = stmtHadith.executeQuery(selectQuery);

            while (rsH.next()) {
                String reference = rsH.getString(1) + ":" + rsH.getString(2) + ":" + rsH.getString(3);

                if (!oldAppReference.contains("2:" + reference)) {
                    app_reference.append(",").append("9:" + reference);
                }
            }

            rsH.close();

            if (app_reference.length() > 0) {
                oldAppReference = oldAppReference + app_reference;
                if (oldAppReference.startsWith(",")) {
                    oldAppReference = oldAppReference.replaceFirst(",", "");
                }

                String query = "update duadetails set app_reference = '" + oldAppReference + "' where _id=" + id + " and reference !=''";
                PreparedStatement preparedStatement = connectionHisnul.prepareStatement(query);
                preparedStatement.executeUpdate();
                System.out.println("Updated reference at " + id + ": " + app_reference);
            }
        }


        System.out.println("Insert success");
        rs.close();

        connectionHadith.close();
        connectionHisnul.close();
    }

    private static void matchUsingDiceCoef() throws SQLException {
        Connection connectionHisnul = DriverManager.getConnection("jdbc:sqlite:D:\\hisnulbd.db");
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

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
        String selectQuery = "select CollectionID, BookID, HadithID, text_ar_diacless from hadiths";

        PreparedStatement stmtHadith = connectionHadith.prepareStatement(selectQuery);

        while (rs.next()) {
            long t = System.currentTimeMillis();
            String arabic = ArabicUtils.normalize(rs.getString(2).replace("(", "").replace(")", ""));

            if (arabic.isEmpty() || arabic.length() < 10) {
                continue;
            }

            Map<String, Integer> arabicProfile = cos.getProfile(arabic);

            String id = rs.getString(1);
            String oldAppReference = rs.getString(3);

            StringBuilder app_reference = new StringBuilder();
            ResultSet resultSet = stmtHadith.executeQuery();

            while (resultSet.next()) {
                String text_ar_diacless = resultSet.getString(1);

                //if length is too much far that means they are really not similar
                if (Math.abs(arabic.length() - text_ar_diacless.length()) > 100) {
                    continue;
                }

                double similarity = cos.similarity(arabicProfile, cos.getProfile(text_ar_diacless));
                if (similarity > 0.9) {
                    String reference = resultSet.getString(2) + ":" + resultSet.getString(3) + ":" + resultSet.getString(4);

                    if (oldAppReference.contains("9:" + reference) || oldAppReference.contains("8:" + reference) || oldAppReference.contains("7:" + reference)) {
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

                String query = "update duadetails set app_reference = '" + oldAppReference + "' where _id=" + id + " and reference !=''";
                PreparedStatement preparedStatement = connectionHisnul.prepareStatement(query);
                preparedStatement.executeUpdate();
                System.out.println("Updated reference at " + id + ": " + app_reference);
            }

            System.out.println("Done " + id + " in " + (System.currentTimeMillis() - t) + " ms");
        }

        rs.close();
        connectionHadith.close();
        connectionHisnul.close();
    }
}
