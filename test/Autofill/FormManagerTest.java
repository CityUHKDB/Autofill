package Autofill;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
    public void testAddFormSuccessful() throws SQLException {
        FormManager formManager = FormManager.getInstance();
        boolean result = formManager.addForm("123.pdf");
        assertEquals(true, result);       
    }
    
    @Test 
    public void testAddFormFail() throws SQLException {
        FormManager formManager = FormManager.getInstance();
        boolean result = formManager.addForm(null);
        assertEquals(false, result);
    }
    
    @Test 
    public void testDeleteForm() throws SQLException {
        FormManager formManager = FormManager.getInstance();
        boolean result = formManager.deleteForm("123.pdf");
        assertEquals(true, result);
    }
    
    @Test
    public void testGetFormList() throws SQLException {
        FormManager formManager = FormManager.getInstance();
        ArrayList<PDFForm> formList = formManager.getFormList();
        assertEquals(false, formList.isEmpty());
    }
}
