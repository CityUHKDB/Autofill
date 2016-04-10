package Autofill;

import com.itextpdf.text.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
@PrepareForTest({DBUtil.class, Dictionary.class})
public class FormFillerTest {
    
    public FormFillerTest() {
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


    //@Test
    public void testFillPdf() throws Exception {
        System.out.println("fillPdf");
        String dest = "";
        String data = "";
        FormFiller instance = null;
        ArrayList<AcroFormField> expResult = null;
        ArrayList<AcroFormField> result = instance.fillPdf(dest, data);
        assertEquals(expResult, result);
    }
    
    // A > B1 > C1 > D1
    @Test
    public void testIsGroupMatch1() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        String synonymSql = "SELECT synonym FROM Dictionary WHERE standard = ? OR standard = ? ORDER BY probability";
        String standardSql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";

        Connection synonymCon = mock(Connection.class);
        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(true).thenReturn(false);
        when(synonymRs.getString("synonym")).thenReturn("word");
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(synonymCon.prepareStatement(synonymSql)).thenReturn(synonymPstmt);
        
        Connection standardCon = mock(Connection.class);
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(true);
        when(standardRs.getString("standard")).thenReturn("word");
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(standardCon.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(standardCon).thenReturn(synonymCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);
        
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("isGroupMatch", AcroFormField.class, String.class);
        method.setAccessible(true);
        AcroFormField field = new AcroFormField();
        String group = "";
        boolean isGroupMatch = (boolean)method.invoke(formfiller, field, group);
        assertEquals(true, isGroupMatch);
    }
    
    // A > B2 > C2 > D1
    @Test
    public void testIsGroupMatch2() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        String synonymSql = "SELECT synonym FROM Dictionary WHERE standard = ? OR standard = ? ORDER BY probability";
        String standardSql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";

        Connection synonymCon = mock(Connection.class);
        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(true).thenReturn(false);
        when(synonymRs.getString("synonym")).thenReturn("word");
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(synonymCon.prepareStatement(synonymSql)).thenReturn(synonymPstmt);
        
        Connection standardCon = mock(Connection.class);
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(true);
        when(standardRs.getString("standard")).thenReturn("word");
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(standardCon.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(standardCon).thenReturn(synonymCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);
        
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("isGroupMatch", AcroFormField.class, String.class);
        method.setAccessible(true);
        AcroFormField field = new AcroFormField();
        field.setGroup("same");
        String group = "same";
        boolean isGroupMatch = (boolean)method.invoke(formfiller, field, group);
        assertEquals(true, isGroupMatch);
    }
    
    // A > B2 > C2 > D2 > EX
    @Test
    public void testIsGroupMatch3() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        String synonymSql = "SELECT synonym FROM Dictionary WHERE standard = ? OR standard = ? ORDER BY probability";
        String standardSql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";

        Connection synonymCon = mock(Connection.class);
        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(true).thenReturn(false);
        when(synonymRs.getString("synonym")).thenReturn("work experience");
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(synonymCon.prepareStatement(synonymSql)).thenReturn(synonymPstmt);
        
        Connection standardCon = mock(Connection.class);
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(true);
        when(standardRs.getString("standard")).thenReturn("work experience");
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(standardCon.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(standardCon).thenReturn(synonymCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);
        
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("isGroupMatch", AcroFormField.class, String.class);
        method.setAccessible(true);
        AcroFormField field = new AcroFormField();
        field.setGroup("work experience");
        String group = "work exp";
        boolean isGroupMatch = (boolean)method.invoke(formfiller, field, group);
        assertEquals(true, isGroupMatch);
    }    
    
    // A > B2 > C2 > D2 > EX > F
    @Test
    public void testIsGroupMatch4() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        String synonymSql = "SELECT synonym FROM Dictionary WHERE standard = ? OR standard = ? ORDER BY probability";
        String standardSql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";

        Connection synonymCon = mock(Connection.class);
        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(false);
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(synonymCon.prepareStatement(synonymSql)).thenReturn(synonymPstmt);
        
        Connection standardCon = mock(Connection.class);
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(false);
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(standardCon.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(standardCon).thenReturn(synonymCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);
        
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("isGroupMatch", AcroFormField.class, String.class);
        method.setAccessible(true);
        AcroFormField field = new AcroFormField();
        field.setGroup("abc");
        String group = "edf";
        boolean isGroupMatch = (boolean)method.invoke(formfiller, field, group);
        assertEquals(false, isGroupMatch);
    }

    // Is on the same line
    @Test
    public void testIsOnSameLineTrue() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("isOnSameLine", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(30, 10, 40, 20);
        Rectangle rectangle2 = new Rectangle(10, 10, 20, 20);
        boolean isOnSameLine = (boolean)method.invoke(formfiller, rectangle1, rectangle2);
        assertEquals(true, isOnSameLine);
    }
    
    // Is not on the same line
    @Test
    public void testIsOnSameLineFalse() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("isOnSameLine", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(10, 40, 20, 50);
        Rectangle rectangle2 = new Rectangle(10, 10, 20, 20);
        boolean isOnSameLine = (boolean)method.invoke(formfiller, rectangle1, rectangle2);
        assertEquals(false, isOnSameLine);
    }
    
    // Decode group name with numbering
    @Test
    public void testDecodeGroupName1() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("decodeGroupName", String.class);
        method.setAccessible(true);
        String resultGroupName = (String)method.invoke(formfiller, "Group Name 1");
        String expectedGroupName = "Group Name";
        assertEquals(expectedGroupName, resultGroupName);
    }   
    
    // Decode group name without numbering (exception thrown)
    @Test
    public void testDecodeGroupName2() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FormFiller formfiller = new FormFiller("");
        Method method = FormFiller.class.getDeclaredMethod("decodeGroupName", String.class);
        method.setAccessible(true);
        String resultGroupName = (String)method.invoke(formfiller, "Group Name");
        String expectedGroupName = "Group Name";
        assertEquals(expectedGroupName, resultGroupName);
    }  
}
