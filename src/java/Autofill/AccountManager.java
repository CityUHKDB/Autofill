package Autofill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AccountManager {
    
    private static AccountManager instance;
    
    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }
    
    private AccountManager() {
        
    }
    
    public User authenticate(String username, String password) throws SQLException {
        User user = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        DBUtil dbUtil = DBUtil.getInstance();
        try {
            con = dbUtil.getDBConnection();
            pstmt = con.prepareStatement(
                "SELECT role, data, fieldGroup FROM User WHERE username = ? AND password = ?"
            );
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setUsername(username);
                user.setRole(rs.getString("role"));
                user.setData(rs.getString("data"));
                user.setGroup(rs.getString("fieldGroup"));
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            dbUtil.closeDBObjects(con, pstmt, rs);
        }
        
        return user;
    }
    
    // User registration
    // Return true if success, otherwise return false
    public Boolean register(String username, String password) throws SQLException {
        Boolean successful = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        DBUtil dbUtil = DBUtil.getInstance();
        try {
            if (!username.equals("") && !password.equals("")) {
                con = dbUtil.getDBConnection();
                pstmt = con.prepareStatement(
                    "INSERT INTO User (username, password, role, data, fieldGroup) VALUES (?, ?, ?, ?, ?)"
                );
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, "member");
                pstmt.setString(4, "[]");
                pstmt.setString(5, "[]");
                pstmt.executeUpdate();

                successful = true;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            dbUtil.closeDBObjects(con, pstmt, rs);
        }
        
        return successful;
    }
    
    public boolean deleteAccount(String username) throws SQLException {
        Boolean successful = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        DBUtil dbUtil = DBUtil.getInstance();
        try {
            con = dbUtil.getDBConnection();
            pstmt = con.prepareStatement(
                "DELETE FROM User WHERE username = ?"
            );
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            successful = true;
        } catch (SQLException e) {
            throw e;
        } finally {
            dbUtil.closeDBObjects(con, pstmt, rs);
        }
        
        return successful;
    }
    
    public ArrayList<User> getUserList() throws SQLException {
        ArrayList<User> userList = new ArrayList();
        
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        DBUtil dbUtil = DBUtil.getInstance();
        try {
            con = dbUtil.getDBConnection();
            pstmt = con.prepareStatement(
                "SELECT * FROM User ORDER BY username"
            );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                userList.add(user);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            dbUtil.closeDBObjects(con, pstmt, rs);
        }
        
        return userList;
    }
}
