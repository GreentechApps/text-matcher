import java.sql.*;

public class CorpusSegmentstoWords {

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Load driver success");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:D:\\corpus.db");


            Statement stmt = connection.createStatement();

            stmt.execute("update corpus set ar1=null, ar2=null, ar3=null, ar4=null, ar5=null,des1=null, des2=null, des3=null, des4=null, des5=null,pos1=null, pos2=null, pos3=null, pos4=null, pos5=null");


            ResultSet rs = stmt.executeQuery("select * from corpus_web");

            while (rs.next()) {
                String surayahwordsegment = rs.getString(1).replace("(", "").replace(")", "");

                String[] splited = surayahwordsegment.split(":");
                int sura = Integer.parseInt(splited[0]);
                int aya = Integer.parseInt(splited[1]);
                int word = Integer.parseInt(splited[2]);
                int segment = Integer.parseInt(splited[3]);

//mustafa furcan.co corpus
                String arabic = rs.getString(2);
                String pos = rs.getString(3);
                String description = rs.getString(4);

                String root = null, lemma = null, vf = null, lemma_form = null;


                if (description.contains("LEM")) {
                    final int lemmaStartIndex = description.indexOf("LEM:");
                    description = description + "|";
                    lemma = description.substring(lemmaStartIndex, description.indexOf("|", lemmaStartIndex));

                    description = description.replace(lemma + "|", "");
                    lemma = lemma.replace("LEM:", "");
                }

                if (description.contains("VF")) {
                    final int vfIndexStart = description.indexOf("VF:");
                    vf = description.substring(vfIndexStart, description.indexOf("|", vfIndexStart));
                    description = description.replace(vf + "|", "");
                    vf = vf.replace("VF:", "");
                }

                if (description.contains("ROOT")) {
                    final int rootIndexStart = description.indexOf("ROOT:");
                    root = description.substring(rootIndexStart, description.indexOf("|", rootIndexStart));
                    description = description.replace(root + "|", "");
                    root = root.replace("ROOT:", "");

                    lemma_form = description;
                }

                //Query to set values
                String query = "update corpus set ar" + segment + "=\"" + arabic
                        + "\" , pos" + segment + "=\"" + pos
                        + "\", des" + segment + "=\"" + description
                        + "\", root_ar='" + root
                        + "', lemma='" + lemma
                        + "', verf_form='" + vf
                        + "', lemma_form='" + lemma_form

                        + "' where surah=" + sura + " and ayah=" + aya + " and word=" + word;

                if (word == 1) {
                    System.out.println("Insert success " + sura + ":" + aya + " " + query);
                }

                //Create prepare statement
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
            }


            System.out.println("Insert success");
            rs.close();


//update count
            stmt.execute("update corpus set count=1 where ar1!=null and ar2=null and ar3=null and ar4=null and ar5=null");
            stmt.execute("update corpus set count=2 where ar1!=null and ar2!=null and ar3=null and ar4=null and ar5=null");
            stmt.execute("update corpus set count=3 where ar1!=null and ar2!=null and ar3!=null and ar4=null and ar5=null");
            stmt.execute("update corpus set count=4 where ar1!=null and ar2!=null and ar3!=null and ar4!=null and ar5=null");
            stmt.execute("update corpus set count=5 where ar1!=null and ar2!=null and ar3!=null and ar4!=null and ar5!=null");

            System.out.println("Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
