package Autofill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.easymock.EasyMock.expect;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DBUtil.class, AccountManager.class})
public class AccountManagerTest {
    
    public AccountManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
 
    // Successful login
    @Test
    public void testAuthenticateSuccessful() throws SQLException {
        String username = "user1";
        String password = "123";
        String role = "member";
        String data = "[]";
        String group = "[]";
                
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);  
        when(rs.next()).thenReturn(true);
        when(rs.getString("role")).thenReturn(role);
        when(rs.getString("data")).thenReturn(data);
        when(rs.getString("fieldGroup")).thenReturn(group);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement("SELECT role, data, fieldGroup FROM User WHERE username = ? AND password = ?")).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        AccountManager authentication = AccountManager.getInstance();
        User user = authentication.authenticate(username, password);
        assertEquals(username, user.getUsername());
        assertEquals(role, user.getRole());
        assertEquals(data, user.getData());
        assertEquals(group, user.getGroup());
    }
    
    // Fail login with wrong username and password
    @Test
    public void testAuthenticateFailWrong() throws SQLException {
        String username = "abcd";
        String password = "123";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);  
        when(rs.next()).thenReturn(false);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement("SELECT role, data, fieldGroup FROM User WHERE username = ? AND password = ?")).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        AccountManager authentication = AccountManager.getInstance();
        User user = authentication.authenticate(username, password);
        assertEquals(null, user);
    }
    
    // Database connection error
    @Test(expected = SQLException.class)
    public void testAuthenticateThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(new SQLException());
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);

        AccountManager authentication = AccountManager.getInstance();
        authentication.authenticate("", "");
    }
    
    // Successful registration
    @Test
    public void testRegisterSuccessful() throws SQLException {
        String username = "user2";
        String password = "123";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement("INSERT INTO User (username, password, role, data, fieldGroup) VALUES (?, ?, ?, ?, ?)")).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(true, result);
    }
    
    // Fail registration with empty password
    @Test
    public void testRegisterFail() throws SQLException {
        String username = "";
        String password = "123";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(pstmt.executeUpdate()).thenThrow(SQLException.class);
        when(con.prepareStatement("INSERT INTO User (username, password, role, data, fieldGroup) VALUES (?, ?, ?, ?, ?)")).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(false, result);
    }
    
    // Fail registration with empty password
    @Test
    public void testRegisterEmpty() throws SQLException {
        String username = "";
        String password = "";
        
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(false, result);
    }
    
    // Database connection error
    @Test(expected = SQLException.class)
    public void testRegisterThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        AccountManager authentication = AccountManager.getInstance();
        authentication.register("user1", "123");
    }
    
    // Successfully delete account
    @Test
    public void testDeleteAccount() throws SQLException {
        String username = "user2";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement("DELETE FROM User WHERE username = ?")).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.deleteAccount(username);
        assertEquals(true, result);
    }
 
    // Database connection error
    @Test(expected = SQLException.class)
    public void testDeleteAccountThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        AccountManager authentication = AccountManager.getInstance();
        authentication.deleteAccount("");
    }
}
