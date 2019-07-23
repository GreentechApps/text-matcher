import stringsimilarity.Cosine;

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

//            matchUsingSqliteMatch();
            matchUsingDiceCoef();

            System.out.println("Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void matchUsingSqliteMatch() throws SQLException {
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

        Statement stmtHadith = connectionHadith.createStatement();

        PreparedStatement statement = connectionHadith.prepareStatement("select rowid as id, text_ar_diacless, related_en from hadiths");
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            long t = System.currentTimeMillis();
            String id = rs.getString(1);
            String arabic = ArabicUtils.normalize(rs.getString(2));

            if (arabic.isEmpty()) {
                continue;
            }

            String oldAppReference = rs.getString(3);
            StringBuilder related_en = new StringBuilder();
            String selectQuery = "select CollectionID, BookID, HadithID from hadiths where text_ar_diacless match '" + arabic + "' and rowid!=" + id;

            ResultSet rsH = stmtHadith.executeQuery(selectQuery);

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

                String query = "update hadiths set related_en = '" + oldAppReference + "' where rowid=" + id;
                PreparedStatement preparedStatement = connectionHadith.prepareStatement(query);
                preparedStatement.executeUpdate();
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
//            SorensenDice sd = new SorensenDice(2);
        Cosine cos = new Cosine(2);

        ArrayList<HadithObject> hadithObjects = new ArrayList<>(45146);
        long total = System.currentTimeMillis();
        while (rs.next()) {
            String arabic = ArabicUtils.normalize(rs.getString(5).replace("(", "").replace(")", ""));

            if (arabic.isEmpty() || arabic.length() < 10) {
                continue;
            }
            String reference = rs.getString(2) + ":" + rs.getString(3) + ":" + rs.getString(4);
            hadithObjects.add(new HadithObject(cos.getProfile(arabic), rs.getLong(1), reference, rs.getString(6)));
        }

        System.out.println("Added to memory Done " + (System.currentTimeMillis() - total) + " ms");

        for (HadithObject currentHadith : hadithObjects) {
            long t1 = System.currentTimeMillis();
            for (HadithObject checkForMatchHadith : hadithObjects) {

                String oldAppReference = currentHadith.getRelated_en();
                StringBuilder related_en = new StringBuilder();
                //if length is too much far that means they are really not similar
                if (Math.abs(currentHadith.getProfile().size() - checkForMatchHadith.getProfile().size()) > 100) {
                    continue;
                }

                double similarity = cos.similarity(currentHadith.getProfile(), checkForMatchHadith.getProfile());
                if (similarity > 0.9) {

                    if (oldAppReference.contains("9:" + checkForMatchHadith.getReference())) {
                        continue;
                    }

                    related_en.append(",").append("7:" + checkForMatchHadith.getReference());

                    System.out.println("Found text similariry of " + similarity + " at " + checkForMatchHadith.getReference() + " for " + currentHadith.getRowid());
                }

                if (related_en.length() > 0) {
                    oldAppReference = oldAppReference + related_en;
                    if (oldAppReference.startsWith(",")) {
                        oldAppReference = oldAppReference.replaceFirst(",", "");
                    }

                    String query = "update hadiths set related_en = '" + oldAppReference + "' where rowid=" + currentHadith.getRowid();
                    PreparedStatement preparedStatement = connectionHadith.prepareStatement(query);
//                    preparedStatement.executeUpdate();
                    System.out.println("Updated reference at " + currentHadith.getRowid() + ": " + related_en);
                }
            }

            System.out.println("Done " + currentHadith.getRowid() + " in " + (System.currentTimeMillis() - t1) + " ms");
        }

        rs.close();
        connectionHadith.close();
        System.out.println("Done Total in " + (System.currentTimeMillis() - total) + " ms");
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

