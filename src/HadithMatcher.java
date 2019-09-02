import stringsimilarity.SorensenDice;

import java.sql.*;
import java.util.ArrayList;

public class HadithMatcher {


    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Load driver success");


            matchUsingDiceCoef();

            System.out.println("Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void matchUsingDiceCoef() throws SQLException {
        Connection connectionMusnad = DriverManager.getConnection("jdbc:sqlite:D:\\musnad.db");

        Statement stmtHadith = connectionMusnad.createStatement();

        ResultSet rs = stmtHadith.executeQuery("select rowid, * from musnad");
        SorensenDice sd = new SorensenDice(2);

        ArrayList<HadithObject> hadithObjects = new ArrayList<>();
        long total = System.currentTimeMillis();
        while (rs.next()) {

            String en = rs.getString(6) + rs.getString(7);
            String ar_text = rs.getString(9);
            String ar_en_google = rs.getString(12);

            hadithObjects.add(new HadithObject(rs.getLong(1), rs.getInt(2), rs.getInt(3), en, ar_text, ar_en_google));
        }

        System.out.println("Added to memory Done " + hadithObjects.size() + " " + (System.currentTimeMillis() - total) + " ms");

        String query = "update musnad set text_ar1 = ? where rowid=?";
        PreparedStatement preparedStatement = connectionMusnad.prepareStatement(query);

        for (int i = 0; i < hadithObjects.size(); i++) {
            HadithObject currentHadith = hadithObjects.get(i);
            long t1 = System.currentTimeMillis();


            if (currentHadith.getRowid() != 114) {
                continue;
            }
            for (HadithObject checkForMatchHadith : hadithObjects) {

                //same hadith
//                if (currentHadith.getRowid() == checkForMatchHadith.getRowid()) {
//                    continue;
//                }
//
//                //cannot be in a differetn collection
//                if (currentHadith.getCollectionID() != checkForMatchHadith.getCollectionID()) {
//                    continue;
//                }
//
//                //cannot be in a different book
//                if (currentHadith.getBookID() != checkForMatchHadith.getBookID()) {
//                    continue;
//                }

                if (checkForMatchHadith.getAr_en_googele() == null) {
                    continue;
                }

                double similarity = sd.similarity(currentHadith.getEn_text(), checkForMatchHadith.getAr_en_googele());
//                if (similarity > 0.75) {


                System.out.println("Found similariry of " + similarity + " at " + checkForMatchHadith.getRowid() + " for " + currentHadith.getRowid());
//                }
            }

//            if (related_en.length() > 0) {
//
//                preparedStatement.setString(1, oldAppReference);
//                preparedStatement.setLong(2, currentHadith.getRowid());
//                preparedStatement.executeUpdate();
//                System.out.println("Updated reference at " + currentHadith.getRowid() + ": " + related_en);
//            }
            System.out.println("Done " + currentHadith.getRowid() + " in " + (System.currentTimeMillis() - t1) + " ms");
        }
//        preparedStatement.executeBatch();

        rs.close();
        connectionMusnad.close();
        System.out.println("Done Total in " + (System.currentTimeMillis() - total) + " ms");
    }


    static class HadithObject {

        private final String en_text;
        private final String ar_test;
        private final String ar_en_googele;
        long rowid;
        private int collectionID;
        private int bookID;


        public HadithObject(long rowid, int collectionID, int bookID, String en_text, String ar_test, String ar_en_googele) {
            this.rowid = rowid;
            this.collectionID = collectionID;
            this.bookID = bookID;
            this.en_text = en_text;
            this.ar_test = ar_test;
            this.ar_en_googele = ar_en_googele;

        }

        public int getBookID() {
            return bookID;
        }

        public int getCollectionID() {
            return collectionID;
        }

        public String getEn_text() {
            return en_text;
        }

        public String getAr_test() {
            return ar_test;
        }

        public String getAr_en_googele() {
            return ar_en_googele;
        }

        public long getRowid() {
            return rowid;
        }


    }
}

