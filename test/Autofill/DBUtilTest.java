package Autofill;

import java.sql.Connection;
import java.sql.DriverManager;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, DBUtil.class})
public class DBUtilTest {
    
    private static final String DBURL = "jdbc:mysql://127.0.0.1:3307/Autofill";
    private static final String DBUsername = "root";
    private static final String DBPassword = "admin";
    
    public DBUtilTest() {
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

    // Get database connection from DriverManager
    @Test
    public void testGetDBConnection() throws SQLException {
        Connection con = mock(Connection.class);
        mockStatic(DriverManager.class);
        expect(DriverManager.getConnection(DBURL, DBUsername, DBPassword)).andReturn(con);
        replay(DriverManager.class);
        
        DBUtil dbUtil = DBUtil.getInstance();
        assertEquals(con, dbUtil.getDBConnection());
    }
    
    // Close Connection, PreparedStatement and ResultSet objects
    // ReseltSet is not null, PreparedStatement and Connection are null
    @Test
    public void testCloseDBObjects1() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        
        DBUtil dbUtil = DBUtil.getInstance();
        dbUtil.closeDBObjects(null, null, rs);
    }
    
    // ReseltSet and PreparedStatement are not null, Connection is null
     @Test
    public void testCloseDBObjects2() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        
        DBUtil dbUtil = DBUtil.getInstance();
        dbUtil.closeDBObjects(null, pstmt, rs);
    }   
    
    // ReseltSet and PreparedStatement are not null, Connection is null
     @Test
    public void testCloseDBObjects3() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        Connection con = mock(Connection.class);
        
        DBUtil dbUtil = DBUtil.getInstance();
        dbUtil.closeDBObjects(con, pstmt, rs);
    }
    
    // Exception throw when close object
    @Test(expected = SQLException.class)
    public void testCloseDBObjectsThrow() throws SQLException {
        assertEquals(true, true);
        ResultSet rs = mock(ResultSet.class);
        doThrow(new SQLException()).when(rs).close();

        DBUtil dbUtil = DBUtil.getInstance();
        dbUtil.closeDBObjects(null, null, rs);
    }  
}
