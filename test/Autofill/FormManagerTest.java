package Autofill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
@PrepareForTest({DBUtil.class, FormManager.class})
public class FormManagerTest {
    
    public FormManagerTest() {
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

    @Test
    public void testAddForm() throws SQLException {
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement("INSERT INTO Form VALUES (?, ?)")).thenReturn(pstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        FormManager formManager = FormManager.getInstance();
        boolean result = formManager.addForm("123.pdf");
        assertEquals(true, result);       
    }
    
    @Test(expected = SQLException.class)
    public void testAddFormThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(new SQLException());
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        FormManager formManager = FormManager.getInstance();
        formManager.addForm(null);
    }
    
    @Test 
    public void testDeleteForm() throws SQLException {
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement("DELETE FROM Form WHERE name = ?")).thenReturn(pstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        FormManager formManager = FormManager.getInstance();
        boolean result = formManager.deleteForm("123.pdf");
        assertEquals(true, result);
    }
    
    @Test(expected = SQLException.class)
    public void testDeleteFormThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(new SQLException());
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        FormManager formManager = FormManager.getInstance();
        formManager.deleteForm(null);        
    }
    
    @Test
    public void testGetFormList() throws SQLException {
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement("SELECT * FROM Form ORDER BY name")).thenReturn(pstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        FormManager formManager = FormManager.getInstance();
        ArrayList<PDFForm> formList = formManager.getFormList();
        assertEquals(false, formList.isEmpty());
    }
 
    @Test(expected = SQLException.class)
    public void testGetFormListThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(new SQLException());
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        FormManager formManager = FormManager.getInstance();
        formManager.getFormList(); 

    }    
}
