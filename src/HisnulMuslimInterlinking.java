import stringsimilarity.Cosine;
import stringsimilarity.SorensenDice;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class HisnulMuslimInterlinking {


    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Load driver success");
//            Connection connectionHisnul = DriverManager.getConnection("jdbc:sqlite:D:\\hisnulbd.db");
//            Statement stmtHisnul = connectionHisnul.createStatement();
//
//            stmtHisnul.execute("update duadetails set app_reference ='' where app_reference is null ");
//
//            stmtHisnul.execute("UPDATE duadetails SET arabic = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(arabic, 'ِ', ''), 'َ' , ''), 'ّ' , ''), 'ً' , ''), 'ٍ' , '') , 'ٌ' , '') , 'ْ' , '') , 'ٓ' , '') , 'ُ' , '')");

//            normalise text
//            stmtHisnul.execute("UPDATE duadetails SET arabic = replace(replace(replace(replace(replace(replace(replace(replace(replace(arabic,'آ','ا'),'أ','ا'),'ؤ','ء'),'إ','ا'),'ئ','ء'),'ا','ا'),'ى','ي'),'ة','ه'),'گ','ك')");
//
//            remove puntuations
//            stmtHisnul.execute("UPDATE duadetails SET arabic = replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(arabic,'ـ',''),'\"',''),'(',''),')',''),'*',''),'،',''),',',''),'-',''),'.',''),'{',''),'}',''),'?',''),'_','')");

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

        ResultSet rs = stmtHisnul.executeQuery("select _id, arabic, app_reference from duadetails where _id=328");
        String query = "update duadetails set app_reference = ? where _id=? and reference !=''";
        PreparedStatement preparedStatementUpdate = connectionHisnul.prepareStatement(query);
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

                if (oldAppReference.contains("2:" + reference) || oldAppReference.contains("9:" + reference) || oldAppReference.contains("8:" + reference)) {
                    continue;
                }

                app_reference.append(",").append("2:" + reference);
            }

            rsH.close();

            if (app_reference.length() > 0) {
                oldAppReference = oldAppReference + app_reference;
                if (oldAppReference.startsWith(",")) {
                    oldAppReference = oldAppReference.replaceFirst(",", "");
                }

                preparedStatementUpdate.setString(1, oldAppReference);
                preparedStatementUpdate.setLong(2, Long.parseLong(id));
//                preparedStatementUpdate.executeUpdate();
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

//            SorensenDice sd = new SorensenDice(2);
        Cosine cos = new Cosine(2);

        Statement stmtHadith = connectionHadith.createStatement();

        ResultSet rsH = stmtHadith.executeQuery("select rowid as id, CollectionID, BookID, HadithID, text_ar_diacless, related_en from hadiths");

        ArrayList<HadithInterlinking.HadithObject> hadithObjects = new ArrayList<>(45146);
        long total = System.currentTimeMillis();

        while (rsH.next()) {
            String arabic = ArabicUtils.normalize(rsH.getString(5).replace("(", "").replace(")", ""));

            if (arabic.isEmpty() || arabic.length() < 10) {
                continue;
            }
            String reference = rsH.getString(2) + ":" + rsH.getString(3) + ":" + rsH.getString(4);
            hadithObjects.add(new HadithInterlinking.HadithObject(cos.getProfile(arabic), rsH.getLong(1), reference, rsH.getString(6)));
        }
        rsH.close();

        ResultSet rsHisnul = stmtHisnul.executeQuery("select _id, arabic, app_reference from duadetails where _id=328");
        PreparedStatement preparedStatementUpdate = connectionHisnul.prepareStatement("update duadetails set app_reference = ? where _id=? and reference !=''");

        while (rsHisnul.next()) {
            long t = System.currentTimeMillis();
            String arabic = ArabicUtils.normalize(rsHisnul.getString(2).replace("(", "").replace(")", ""));

            if (arabic.isEmpty() || arabic.length() < 10) {
                continue;
            }

            Map<String, Integer> arabicProfile = cos.getProfile(arabic);
            String id = rsHisnul.getString(1);
            String oldAppReference = rsHisnul.getString(3);

            StringBuilder related_en = new StringBuilder();

            for (HadithInterlinking.HadithObject checkForMatchHadith : hadithObjects) {

                //if length is too much far that means they are really not similar
                if (Math.abs(arabicProfile.size() - checkForMatchHadith.getProfile().size()) > 100) {
                    continue;
                }

                if (oldAppReference.contains("2:" + checkForMatchHadith.getReference()) || oldAppReference.contains("9:" + checkForMatchHadith.getReference()) || oldAppReference.contains("8:" + checkForMatchHadith.getReference())) {
                    continue;
                }

                double similarity = cos.similarity(arabicProfile, checkForMatchHadith.getProfile());
                if (similarity > 0.9) {
                    related_en.append(",").append("8:" + checkForMatchHadith.getReference());

                    System.out.println("Found text similariry of " + similarity + " at " + checkForMatchHadith.getReference() + " for " + id);
                }
            }

            if (related_en.length() > 0) {
                oldAppReference = oldAppReference + related_en;
                if (oldAppReference.startsWith(",")) {
                    oldAppReference = oldAppReference.replaceFirst(",", "");
                }

                preparedStatementUpdate.setString(1, oldAppReference);
                preparedStatementUpdate.setLong(2, Long.parseLong(id));
//                preparedStatementUpdate.executeUpdate();
                System.out.println("Updated reference at " + id + ": " + related_en);
            }

            System.out.println("Done " + id + " in " + (System.currentTimeMillis() - t) + " ms");
        }

        rsHisnul.close();
        connectionHadith.close();
        connectionHisnul.close();
        System.out.println("Done Total in " + (System.currentTimeMillis() - total) + " ms");
    }
}
