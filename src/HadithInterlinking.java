import stringsimilarity.Cosine;
import stringsimilarity.SorensenDice;
import stringsimilarity.SubsectionSimilar;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class HadithInterlinking {


    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Load driver success");

            //        stmtHadith.execute("update hadiths set related_en ='' where related_en is null ");

//        stmtHadith.execute("UPDATE hadiths SET text_ar_diacless = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(text_ar_diacless, 'ِ', ''), 'َ' , ''), 'ّ' , ''), 'ً' , ''), 'ٍ' , '') , 'ٌ' , '') , 'ْ' , '') , 'ٓ' , '') , 'ُ' , '')");
//
//            System.out.println("remove diacritics");
            //normalise text
//        stmtHadith.execute("UPDATE hadiths SET text_ar_diacless = replace(replace(replace(replace(replace(replace(replace(replace(replace(text_ar_diacless,'آ','ا'),'أ','ا'),'ؤ','ء'),'إ','ا'),'ئ','ء'),'ا','ا'),'ى','ي'),'ة','ه'),'گ','ك')");
//        System.out.println("normalise text");

            //remove puntuations
//        stmtHadith.execute("UPDATE hadiths SET text_ar_diacless = replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(text_ar_diacless,'ـ',''),'\"',''),'(',''),')',''),'*',''),'،',''),',',''),'-',''),'.',''),'{',''),'}',''),'?',''),'_','')");

            ///todo need to replace ' ” ”
//        System.out.println("removed puntuations");

            matchUsingSqliteMatch();
            matchUsingDiceCoef();
//            checkStringSimilarityForErros();

            System.out.println("Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void matchUsingSqliteMatch() throws SQLException {
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

        PreparedStatement statement = connectionHadith.prepareStatement("select rowid as id, text_ar_diacless, related_en from hadiths");
        ResultSet rs = statement.executeQuery();

        String query = "select CollectionID, BookID, HadithID from hadiths where text_ar_diacless match ? and rowid!=?";
        PreparedStatement preparedStatementSearch = connectionHadith.prepareStatement(query);
        PreparedStatement preparedStatementUpdate = connectionHadith.prepareStatement("update hadiths set related_en = ? where rowid=?");

        while (rs.next()) {
            long t = System.currentTimeMillis();
            String id = rs.getString(1);
            String arabic = ArabicUtils.normalize(rs.getString(2));

            if (arabic.isEmpty() || arabic.length() < 15) {
                continue;
            }

            String oldAppReference = rs.getString(3);
            StringBuilder related_en = new StringBuilder();

            preparedStatementSearch.setString(1, arabic);
            preparedStatementSearch.setLong(2, Long.parseLong(id));
            ResultSet rsH = preparedStatementSearch.executeQuery();

            while (rsH.next()) {
                String reference = rsH.getString(1) + ":" + rsH.getString(2) + ":" + rsH.getString(3);

                if (!oldAppReference.contains("2:" + reference)) {
                    related_en.append(",").append("2:" + reference);
                }
//                System.out.println("Found text match " + " at " + reference + " for " + id);
            }

            rsH.close();

            if (related_en.length() > 0) {
                oldAppReference = oldAppReference + related_en;
                if (oldAppReference.startsWith(",")) {
                    oldAppReference = oldAppReference.replaceFirst(",", "");
                }

                preparedStatementUpdate.setString(1, oldAppReference);
                preparedStatementUpdate.setLong(2, Long.parseLong(id));
                preparedStatementUpdate.executeUpdate();
                System.out.println("Updated reference at " + id + ": " + related_en);
            }
            System.out.println("Done " + id + " in " + (System.currentTimeMillis() - t) + " ms");
        }

        System.out.println("Insert success");
        rs.close();

        connectionHadith.close();
    }


    private static void matchUsingDiceCoef() throws SQLException {
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

        Statement stmtHadith = connectionHadith.createStatement();

        ResultSet rs = stmtHadith.executeQuery("select rowid as id, CollectionID, BookID, HadithID, text_ar_diacless, related_en from hadiths");
        SorensenDice sd = new SorensenDice(2);
//        Cosine cos = new Cosine(3);
//        SubsectionSimilar sd = new SubsectionSimilar(3);

        ArrayList<HadithObject> hadithObjects = new ArrayList<>(45146);
        long total = System.currentTimeMillis();
        while (rs.next()) {
            String arabic = ArabicUtils.normalize(rs.getString(5).replace("(", "").replace(")", ""));

            if (arabic.isEmpty() || arabic.length() < 10) {
                continue;
            }
            String reference = rs.getString(2) + ":" + rs.getString(3) + ":" + rs.getString(4);
            hadithObjects.add(new HadithObject(sd.getProfile(arabic), rs.getLong(1), reference, rs.getString(6)));
        }

        System.out.println("Added to memory Done " + (System.currentTimeMillis() - total) + " ms");

        String query = "update hadiths set related_en = ? where rowid=?";
        PreparedStatement preparedStatement = connectionHadith.prepareStatement(query);

        for (HadithObject currentHadith : hadithObjects) {
            long t1 = System.currentTimeMillis();
            if (currentHadith.getRowid() > 10) {
                break;
            }

            String oldAppReference = currentHadith.getRelated_en();
            StringBuilder related_en = new StringBuilder();

            for (HadithObject checkForMatchHadith : hadithObjects) {

                //if length is too much far that means they are really not similar
//                if (Math.abs(currentHadith.getProfile().size() - checkForMatchHadith.getProfile().size()) > 100) {
//                    continue;
//                }
                //same hadith
                if (currentHadith.getRowid() == checkForMatchHadith.getRowid()) {
                    continue;
                }

                if (oldAppReference.contains("2:" + checkForMatchHadith.getReference()) || oldAppReference.contains("9:" + checkForMatchHadith.getReference()) || oldAppReference.contains("8:" + checkForMatchHadith.getReference())) {
                    continue;
                }

                double similarity = sd.similarity(currentHadith.getProfile(), checkForMatchHadith.getProfile());
                if (similarity > 0.75) {

                    related_en.append(",").append("9:" + checkForMatchHadith.getReference());

                    System.out.println("Found similariry of " + similarity + " at " + checkForMatchHadith.getReference() + " for " + currentHadith.getRowid());
                }
            }

            if (related_en.length() > 0) {
                oldAppReference = oldAppReference + related_en;
                if (oldAppReference.startsWith(",")) {
                    oldAppReference = oldAppReference.replaceFirst(",", "");
                }

                preparedStatement.setString(1, oldAppReference);
                preparedStatement.setLong(2, currentHadith.getRowid());
                preparedStatement.executeUpdate();
                System.out.println("Updated reference at " + currentHadith.getRowid() + ": " + related_en);
            }
            System.out.println("Done " + currentHadith.getRowid() + " in " + (System.currentTimeMillis() - t1) + " ms");
        }

        rs.close();
        connectionHadith.close();
        System.out.println("Done Total in " + (System.currentTimeMillis() - total) + " ms");
    }


    public static void checkStringSimilarityForErros() throws SQLException {
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

        Statement stmtHadith = connectionHadith.createStatement();

        ResultSet rs = stmtHadith.executeQuery("select rowid as id, CollectionID, BookID, HadithID, text_ar_diacless, related_en from hadiths where rowid in (9)");

        PreparedStatement statement = connectionHadith.prepareStatement("select CollectionID, BookID, HadithID, text_ar_diacless, related_en from hadiths where  CollectionID=? and BookID=? and HadithID=?");

        while (rs.next()) {
            String arabic = ArabicUtils.normalize(rs.getString(5).replace("(", "").replace(")", ""));

            String related_en = rs.getString(6);
            String[] split = related_en.split(",");

            System.out.println("Check similarity for " + rs.getString(1) + " " + related_en);

            ArrayList<String> strings = new ArrayList<>();
            for (String s : split) {
                String[] reference = s.split(":");
                statement.setInt(1, Integer.parseInt(reference[1]));
                statement.setInt(2, Integer.parseInt(reference[2]));
                statement.setInt(3, Integer.parseInt(reference[3]));

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String test1 = resultSet.getString(4);
                    strings.add(test1);
                    System.out.println("for " + resultSet.getString(1) + ":" + resultSet.getString(2) + ":" + resultSet.getString(3));
                }
                resultSet.close();
            }


            StringSimilarityTest.textSimilarityTest(arabic, strings);
        }
        rs.close();
        connectionHadith.close();


    }

    static class HadithObject {

        Map<String, Integer> profile;
        long rowid;
        String reference;
        String related_en;

        public HadithObject(Map<String, Integer> profile, long rowid, String reference, String related_en) {
            this.profile = profile;
            this.rowid = rowid;
            this.reference = reference;
            this.related_en = related_en;
        }

        public Map<String, Integer> getProfile() {
            return profile;
        }


        public long getRowid() {
            return rowid;
        }


        public String getReference() {
            return reference;
        }


        public String getRelated_en() {
            return related_en;
        }
    }
}

