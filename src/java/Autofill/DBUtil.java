package Autofill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
 
    private static final String DBURL = "jdbc:mysql://127.0.0.1:3307/Autofill";
    private static final String DBUsername = "root";
    private static final String DBPassword = "admin";
    
    private static DBUtil instance;
    
    private DBUtil() {
        
    }
    
    public static DBUtil getInstance() {
        if (instance == null) {
            instance = new DBUtil();
        }
        return instance;
    }
    
    public Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(DBURL, DBUsername, DBPassword);
    }
    
    public void closeDBObjects(Connection con, PreparedStatement pstmt) {
        try {
            if (pstmt != null) {pstmt.close();}
            if (con != null) {con.close();}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void closeDBObjects(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {rs.close();}
            if (pstmt != null) {pstmt.close();}
            if (con != null) {con.close();}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
