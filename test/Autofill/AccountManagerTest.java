package Autofill;

import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        AccountManager authentication = AccountManager.getInstance();
        User user = authentication.authenticate(username, password);
        assertEquals(username, user.getUsername());
    }

    // Fail login with null username and passowrd
    @Test
    public void testAuthenticateFailNull() throws SQLException {
        String username = null;
        String password = null;
        AccountManager authentication = AccountManager.getInstance();
        User user = authentication.authenticate(username, password);
        assertEquals(null, user);
    }
    
    // Fail login with wrong username and password
    @Test
    public void testAuthenticateFailWrong() throws SQLException {
        String username = "abcd";
        String password = "123";
        AccountManager authentication = AccountManager.getInstance();
        User user = authentication.authenticate(username, password);
        assertEquals(null, user);
    }
    
    // Successful registration
    @Test
    public void testRegisterSuccessful() throws SQLException {
        String username = "user2";
        String password = "123";
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(true, result);
    }
    
    // Fail registration with null username and password
    @Test
    public void testRegisterFailNull() throws SQLException {
        String username = null;
        String password = null;
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(false, result);
    }
    
    // Fail registration with empty username
    @Test
    public void testRegisterFailEmptyUsername() throws SQLException {
        String username = "";
        String password = "123";
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(false, result);
    }
    
    // Fail registration with empty password
    @Test
    public void testRegisterFailEmptyPassword() throws SQLException {
        String username = "";
        String password = "123";
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(false, result);
    }
    
    // Fail registration with empty username and password
    @Test
    public void testRegisterFailEmptyAdd() throws SQLException {
        String username = "";
        String password = "";
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.register(username, password);
        assertEquals(false, result);
    }
    
    // Successfully delete account
    @Test
    public void testDeleteAccount() throws SQLException {
        String username = "user2";
        AccountManager authentication = AccountManager.getInstance();
        Boolean result = authentication.deleteAccount(username);
        assertEquals(false, result);
    }
}
