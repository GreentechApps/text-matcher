import stringsimilarity.SorensenDice;

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
//            checkStringSimilarityForErros();

            System.out.println("Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void matchUsingSqliteMatch() throws SQLException {
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

        PreparedStatement statement = connectionHadith.prepareStatement("select rowid as id, text_ar_diacless, related_en from hadith");
        ResultSet rs = statement.executeQuery();

        String query = "select CollectionID, BookID, HadithID from hadith where text_ar_diacless match ? and rowid!=?";
        PreparedStatement preparedStatementSearch = connectionHadith.prepareStatement(query);
        PreparedStatement preparedStatementUpdate = connectionHadith.prepareStatement("update hadith set related_en = ? where rowid=?");

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

        ResultSet rs = stmtHadith.executeQuery("select rowid as id, CollectionID, BookID, HadithID, text_ar_diacless, related_en from hadith");
        SorensenDice sd = new SorensenDice(2);
        SorensenDice sd3 = new SorensenDice(3);

        ArrayList<HadithObject> hadithObjects = new ArrayList<>(45146);
        long total = System.currentTimeMillis();
        while (rs.next()) {
            String arabic = ArabicUtils.normalize(rs.getString(5).replace("(", "").replace(")", ""));

            if (arabic.isEmpty() || arabic.length() < 10) {
                continue;
            }
            String reference = rs.getString(2) + ":" + rs.getString(3) + ":" + rs.getString(4);
            hadithObjects.add(new HadithObject(sd.getProfile(arabic), sd3.getProfile(arabic), rs.getLong(1), reference, rs.getString(6)));
        }

        System.out.println("Added to memory Done " + (System.currentTimeMillis() - total) + " ms");

        String query = "update hadith set related_en = ? where rowid=?";
        PreparedStatement preparedStatement = connectionHadith.prepareStatement(query);

        hadithObjects.parallelStream().forEach(currentHadith -> {
            long t1 = System.currentTimeMillis();

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

                double similarityK2 = sd.similarity(currentHadith.getProfileK2(), checkForMatchHadith.getProfileK2());
                double similarityK3 = sd3.similarity(currentHadith.getProfileK3(), checkForMatchHadith.getProfileK3());
                double dif = similarityK2 - similarityK3;
                if (similarityK2 > 0.75 && dif < 0.27) {//ensure similarity is around 75% and the dif is there to ensure similarity is more accurate in larger texts
                    related_en.append(",").append("9:" + checkForMatchHadith.getReference());

                    System.out.println("Found similariry of " + similarityK2 + " at " + checkForMatchHadith.getReference() + " for " + currentHadith.getRowid());
                }
            }

            if (related_en.length() > 0) {
                oldAppReference = oldAppReference + related_en;
                if (oldAppReference.startsWith(",")) {
                    oldAppReference = oldAppReference.replaceFirst(",", "");
                }

                try {
                    preparedStatement.setString(1, oldAppReference);

                    preparedStatement.setLong(2, currentHadith.getRowid());
//                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Updated reference at " + currentHadith.getRowid() + ": " + related_en);
            }
            System.out.println("Done " + currentHadith.getRowid() + " in " + (System.currentTimeMillis() - t1) + " ms");
        });

//        preparedStatement.executeBatch();

        rs.close();
        connectionHadith.close();
        System.out.println("Done Total in " + (System.currentTimeMillis() - total) + " ms");
    }


    public static void checkStringSimilarityForErros() throws SQLException { //checked and corrected for longer texts, still check for 720 , 722, 790 for sqlite match references (starts with 2)
        Connection connectionHadith = DriverManager.getConnection("jdbc:sqlite:D:\\hadith.db");

        Statement stmtHadith = connectionHadith.createStatement();

        ResultSet rs = stmtHadith.executeQuery("select rowid as id, CollectionID, BookID, HadithID, text_ar_diacless, related_en from hadith where length(related_en) >150");

        PreparedStatement statement = connectionHadith.prepareStatement("select CollectionID, BookID, HadithID, text_ar_diacless, related_en from hadith where  CollectionID=? and BookID=? and HadithID=?");
        SorensenDice sd2 = new SorensenDice(2);
        SorensenDice sd3 = new SorensenDice(3);
        String query = "update hadith set related_en = ? where rowid=?";
        PreparedStatement preparedStatement = connectionHadith.prepareStatement(query);
        ArrayList<String> strings = new ArrayList<>();

        while (rs.next()) {
            String arabic = ArabicUtils.normalize(rs.getString(5).replace("(", "").replace(")", ""));
            String rowid = rs.getString(1);
            String related_en = rs.getString(6);
            String[] split = related_en.split(",");

            System.out.println("\nCheck similarity for " + rowid + " " + rs.getString(2) + ":" + rs.getString(3) + ":" + rs.getString(4) + "  Related " + related_en);

            strings.clear();

            for (String s : split) {
                String[] reference = s.split(":");

                if (Integer.parseInt(reference[0]) == 2) { //if the value is 2 means it has been verified as 2 means using sqlite match fucntion , while 9 is text similarity
                    strings.add(s);
                    continue;
                }

                statement.setInt(1, Integer.parseInt(reference[1]));
                statement.setString(2, reference[2]);
                statement.setInt(3, Integer.parseInt(reference[3]));

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String test = resultSet.getString(4);

                    System.out.println("\nfor " + resultSet.getString(1) + ":" + resultSet.getString(2) + ":" + resultSet.getString(3));

                    double similarity = sd2.similarity(arabic, test);
                    double similarity1 = sd3.similarity(arabic, test);
                    System.out.println("Sorensen-Dice: " + similarity);
                    System.out.println("Sorensen-Dice: " + similarity1);
                    double v = similarity - similarity1;
                    if (v < 0.27) {
                        System.out.println("Highly Probable " + v);
                        strings.add(s);
                    } else {
                        System.out.println("No");

                    }

                }
                resultSet.close();
            }
            String new_related_en = strings.toString().replace("[", "").replace("]", "").replace(" ", "");

            if (!related_en.equals(new_related_en)) {
                preparedStatement.setString(1, new_related_en);
                preparedStatement.setLong(2, Long.parseLong(rowid));
                preparedStatement.executeUpdate();
                System.out.println("Updated reference at " + rowid + ": " + strings.toString());
            }
        }
        rs.close();
        connectionHadith.close();


    }

    static class HadithObject {

        final Map<String, Integer> profileK2;
        final long rowid;
        final String reference;
        final String related_en;
        final private Map<String, Integer> profileK3;

        public HadithObject(Map<String, Integer> profileK2, Map<String, Integer> profileK3, long rowid, String reference, String related_en) {
            this.profileK2 = profileK2;
            this.profileK3 = profileK3;
            this.rowid = rowid;
            this.reference = reference;
            this.related_en = related_en;
        }

        public Map<String, Integer> getProfileK2() {
            return profileK2;
        }

        public Map<String, Integer> getProfileK3() {
            return profileK3;
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

