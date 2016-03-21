package Autofill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class FormManager {

    private static FormManager instance;
    private static final String DBURL = "jdbc:mysql://127.0.0.1:3307/Autofill";
    private static final String DBUsername = "root";
    private static final String DBPassword = "admin";
    
    private FormManager() {
        
    }
    
    public static FormManager getInstance() {
        if (instance == null) {
            instance = new FormManager();
        }
        return instance;
    }
    
    public boolean deleteForm(String fileName) throws SQLException {
        boolean successful = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "DELETE FROM Form WHERE name = ?"
            );
            pstmt.setString(1, fileName);
            pstmt.executeUpdate();
            successful = true;
        } catch (SQLException e) {
            //throw e;
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
    
    public boolean addForm(String fileName) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            //Store file information to database
            pstmt = con.prepareStatement(
                "INSERT INTO Form VALUES (?, ?)"
            );
            pstmt.setString(1, fileName);
            pstmt.setString(2, fileName.substring(0, fileName.lastIndexOf('.')));
            pstmt.executeUpdate();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (pstmt != null) {pstmt.close();}
                if (con != null) {con.close();}
            } catch (SQLException e) {
                throw e;
            }
        }

        return true;
    }
    
    public ArrayList<PDFForm> getFormList() throws SQLException {
        ArrayList<PDFForm> formList = new ArrayList();
        
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(DBURL, DBUsername, DBPassword);
            pstmt = con.prepareStatement(
                "SELECT * FROM Form ORDER BY name"
            );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                PDFForm form = new PDFForm();
                form.setName(rs.getString("name"));
                form.setDescription(rs.getString("description"));
                formList.add(form);
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
        
        return formList;
    }
}
