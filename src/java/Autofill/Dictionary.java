package Autofill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Dictionary {
    
    private static Dictionary instance;
    private static final String DBURL = "jdbc:mysql://127.0.0.1:3307/Autofill";
    private static final String DBUsername = "root";
    private static final String DBPassword = "admin";
    
    public static Dictionary getInstance() {
        if (instance == null) {
            instance = new Dictionary();
        }
        return instance;
    }
    
    private Dictionary() {
        
    }
    
    public ArrayList<String> getSynonyms(String word) throws SQLException {
        ArrayList<String> synonyms = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String standard = getStandardWord(word);

            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "SELECT synonym FROM Dictionary WHERE standard = ? OR standard = ? ORDER BY probability"
            );
            pstmt.setString(1, word);
            pstmt.setString(2, standard); 
            rs = pstmt.executeQuery();
            while (rs.next()) {
                synonyms.add(rs.getString("synonym"));
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            }
        }

        return synonyms;
    }
    
    public String getStandardWord(String word) throws SQLException {
        String standard = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability"
            );
            pstmt.setString(1, word);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                standard = rs.getString("standard");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            } 
        }
        
        return standard;
    }
    
    public ArrayList<String> getSamePrefixWords(String word) throws SQLException {
        ArrayList<String> samePrefixWords = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        for (int i=word.length()-1; i>0; i--) {
            if (!Character.isDigit(word.charAt(i)) && word.charAt(i)!='_') {
                //System.out.print(word + " ");
                word = word.substring(0, i+1);
                samePrefixWords.add(word.toLowerCase());
                //System.out.println(word);
                break;
            }
        }
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            // Create search key
            StringBuilder strBuilder = new StringBuilder(word);
            strBuilder.insert(word.length(), "%");
            for (int i=word.length()-1; i>0; i--) {
                if (Character.isUpperCase(word.charAt(i))) {
                    strBuilder.insert(i, " %");
                }
            }
            String searchKey = strBuilder.toString().toLowerCase();

            pstmt = con.prepareStatement(
                "SELECT standard FROM Dictionary WHERE standard LIKE ?"
            );
            pstmt.setString(1, searchKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                samePrefixWords.add(rs.getString("standard"));
            }

            pstmt = con.prepareStatement(
                "SELECT synonym FROM Dictionary WHERE synonym LIKE ?"
            );
            pstmt.setString(1, searchKey);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                samePrefixWords.add(rs.getString("synonym"));
            }
        } catch (SQLException e)    {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (Exception e) {
                throw e;
            }
        }
        
        return samePrefixWords;
    }
    
    public boolean addSynonym(String word1, String word2) throws SQLException {
        boolean successful = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "INSERT INTO Dictionary VALUES (?, ?, ?, ?)"
            );
            pstmt.setString(1, word1);
            pstmt.setString(2, word2);
            pstmt.setFloat(3, 0.5f);
            pstmt.setString(4, "[{\"word1\": \"" + word1 + "\", \"word2\": \"" + word2 + "\", \"probability\": \"0.5\"}]");
            pstmt.executeUpdate();
            System.out.println(pstmt);
            
            successful = true;
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            }
        }
        return successful;
    }
    
    public boolean removeSynonym(String word1, String word2) throws SQLException {
        boolean successful = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "DELETE FROM Dictionary WHERE standard = ? AND synonym = ?"
            );
            pstmt.setString(1, word1);
            pstmt.setString(2, word2);
            System.out.println(pstmt);
            pstmt.executeUpdate();
            successful = true;
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            }
        }
        
        return successful;
    }
    
    public boolean addProbability(String word1, String word2) throws SQLException {
        boolean adjusted = false;
        Float adjustment = 0.1f;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
 
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?"
            );                      
            pstmt.setString(1, word1);
            pstmt.setString(2, word2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                float probability = rs.getFloat("probability");
                String history = rs.getString("history");
                probability = Math.min(1, Math.round((probability + adjustment)*10)/10);
                String newHistory = addHistory(history, formatHistoryRecord(word1, word2, probability));
                pstmt = con.prepareStatement(
                    "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?"    
                );
                pstmt.setFloat(1, probability);
                pstmt.setString(2, newHistory);
                pstmt.setString(3, word1);
                pstmt.setString(4, word2);
                pstmt.executeUpdate();
                adjusted = true;
            }

            if (!adjusted) {
                // Synonym to standard matching
                pstmt = con.prepareStatement(
                    "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?"
                ); 
                pstmt.setString(1, word2);
                pstmt.setString(2, word1);
                System.out.println("TEST4 - " + pstmt);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    System.out.println("TEST4");
                    float probability = rs.getFloat("probability");
                    String history = rs.getString("history");
                    probability = Math.min(1, Math.round((probability + adjustment)*10)/10);
                    String newHistory = addHistory(history, formatHistoryRecord(word2, word1, probability));
                    pstmt = con.prepareStatement(
                        "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?"    
                    );
                    pstmt.setFloat(1, probability);
                    pstmt.setString(2, newHistory);
                    pstmt.setString(3, word2);
                    pstmt.setString(4, word1);
                    System.out.println(pstmt);
                    pstmt.executeUpdate();
                    adjusted = true;
                }
            }
            
            if (!adjusted) {
                // Synonym to synonym matching
                pstmt = con.prepareStatement(
                    "SELECT D1.standard As standard, D1.probability As probability, D1.history As history FROM Dictionary D1, Dictionary D2 WHERE D1.standard = D2.standard AND D1.synonym = ? AND D2.synonym = ?"
                );
                pstmt.setString(1, word2);
                pstmt.setString(2, word1);
                rs = pstmt.executeQuery();
                String standard;
                String history;
                float probability;
                if (rs.next()) {
                    standard = rs.getString("standard");
                    history = rs.getString("history");
                    probability = rs.getFloat("probability");
                    probability = Math.min(1, (float)(Math.round((probability + adjustment)*10))/10);

                    String newHistory = addHistory(history, formatHistoryRecord(word1, word2, probability));
                    pstmt = con.prepareStatement(
                        "UPDATE Dictionary SET probability = ROUND(LEAST(1, probability + 0.1), 1), history = ? WHERE standard = ? AND (synonym = ? OR synonym = ?)"
                    ); 
                    pstmt.setString(1, newHistory);
                    pstmt.setString(2, standard);
                    pstmt.setString(3, word2);
                    pstmt.setString(4, word1);
                    pstmt.executeUpdate();
                    adjusted = true;
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            }
        }
        return adjusted;
        //End
    }
    
    public boolean reduceProbability(String word1, String word2) throws SQLException {
        //Start
        boolean adjusted = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        float adjustment = -0.1f;
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?"
            );
            pstmt.setString(1, word1);
            pstmt.setString(2, word2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                float probability = rs.getFloat("probability");
                String history = rs.getString("history");
                probability = Math.max(0, (float)(Math.round((probability + adjustment)*10))/10);
                if (probability == 0) {
                    removeSynonym(word1, word2);
                } else {
                    // Update probability
                    String newHistory = addHistory(history, formatHistoryRecord(word1, word2, probability));
                    pstmt = con.prepareStatement(
                        "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?"
                    );
                    pstmt.setFloat(1, probability);
                    pstmt.setString(2, newHistory);
                    pstmt.setString(3, word1);
                    pstmt.setString(4, word2);
                    pstmt.executeUpdate();                                        
                }
                adjusted = true;
            } 
            
            if (!adjusted) {
                // Synonym to standard matching
                pstmt = con.prepareStatement(
                    "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?"
                );
                pstmt.setString(1, word2);
                pstmt.setString(2, word1);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    float probability = rs.getFloat("probability");
                    String history = rs.getString("history");
                    probability = Math.max(0, (float)(Math.round((probability + adjustment)*10))/10);
                    if (probability == 0) {
                        // Remove entry
                        removeSynonym(word2, word1);
                    } else {
                        // Update probability
                        String newHistory = addHistory(history, formatHistoryRecord(word1, word2, probability));
                        pstmt = con.prepareStatement(
                            "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?"
                        );
                        pstmt.setFloat(1, probability);
                        pstmt.setString(2, newHistory);
                        pstmt.setString(3, word2);
                        pstmt.setString(4, word1);
                        pstmt.executeUpdate();                                        
                    }
                    adjusted = true;
                }
            }
            
            if (!adjusted) {
                // Synonym to synonym matching
                pstmt = con.prepareStatement(
                    "SELECT D1.standard As standard, D1.probability As probability, D1.history As history FROM Dictionary D1, Dictionary D2 WHERE D1.standard = D2.standard AND D1.synonym = ? AND D2.synonym = ?"
                );
                pstmt.setString(1, word1);
                pstmt.setString(2, word2);
                System.out.println("TEST7 - " + pstmt);
                rs = pstmt.executeQuery();
                String standard;
                String history;
                Float probability;
                if (rs.next()) {
                    standard = rs.getString("standard");
                    history = rs.getString("history");
                    probability = rs.getFloat("probability");
                    probability = (float)(Math.round((probability - 0.1f)*10))/10;
                    if (probability == 0) {
                        //remove entry
                        removeSynonym(standard, word1);
                    } else {
                        String newHistory = addHistory(history, formatHistoryRecord(word1, word2, probability));
                        pstmt = con.prepareStatement(
                            "UPDATE Dictionary SET probability = ROUND(LEAST(1, probability - 0.1), 1), history = ? WHERE standard = ? AND (synonym = ? OR synonym = ?)" //need separated update
                        ); 
                        pstmt.setString(1, newHistory);
                        pstmt.setString(2, standard);
                        pstmt.setString(3, word1);
                        pstmt.setString(4, word2);
                        //System.out.println("TEST6 - " + pstmt);
                        pstmt.executeUpdate();
                    }
                    adjusted = true;
                }
            }
        
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            }
        }
        
        return adjusted;
    }
        
    public ArrayList<Statistics> getHistory() throws SQLException {
        ArrayList<Statistics> statistics = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "SELECT standard, synonym, history FROM Dictionary ORDER BY standard"
            );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Statistics s = new Statistics();
                s.setStandard(rs.getString("standard"));
                s.setSynonym(rs.getString("synonym"));
                if (rs.getString("history") != null) {
                    s.setHistory(rs.getString("history"));
                } else {
                    s.setHistory("[]");
                }
                statistics.add(s);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            }
        }
        
        return statistics;
    }
    
    public String formatHistoryRecord(String word1, String word2, float probability) {
        StringBuilder str = new StringBuilder();
        str.append("{");
        str.append("\"word1\": ");
        str.append("\"");
        str.append(word1);
        str.append("\"");
        str.append(", ");
        str.append("\"word2\": ");
        str.append("\"");
        str.append(word2);
        str.append("\"");
        str.append(", ");
        str.append("\"probability\": ");
        str.append("\"");
        str.append(probability);
        str.append("\"");
        str.append("}");
        return new String(str);
    }

    public String addHistory(String oldHistory, String newHistory) {
        StringBuilder str;
        if (oldHistory.length() > 2) {
            // Have record
            str = new StringBuilder(oldHistory);
            str.replace(str.length()-1, str.length(), ", " + newHistory);
            str.append("]");
        } else {
            // No record
            str = new StringBuilder();
            str.append("[");
            str.append(newHistory);
            str.append("]");
        }
        return new String(str);
    }
}
