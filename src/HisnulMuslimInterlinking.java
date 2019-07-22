import java.sql.*;

public class HisnulMuslimInterlinking {


    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Load driver success");

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
                String arabic = DiacriticInsensitiveSearch.normalize(rs.getString(2).replace("(", "").replace(")", ""));

                if (arabic.isEmpty() || arabic.length() < 15) {
                    continue;
                }

                String oldAppReference = rs.getString(3);
                StringBuilder app_reference = new StringBuilder();
                String selectQuery = "select * from hadiths where text_ar_diacless match '" + arabic + "'";
//                System.out.println(selectQuery);

                ResultSet resultSet = stmtHadith.executeQuery(selectQuery);

                while (resultSet.next()) {
                    String reference = "9:" + resultSet.getString(1) + ":" + resultSet.getString(2) + ":" + resultSet.getString(4);

                    if (!oldAppReference.contains(reference)) {
                        app_reference.append(",").append(reference);
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
            }


            System.out.println("Insert success");
            rs.close();
            connectionHadith.close();
            connectionHisnul.close();


            System.out.println("Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
