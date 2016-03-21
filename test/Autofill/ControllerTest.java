package Autofill;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerTest {
    
    public ControllerTest() {
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

    /**
     * Test of processRequest method, of class Controller.
     */
    @Test
    public void testProcessRequest() throws Exception {
        System.out.println("processRequest");
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        Controller instance = new Controller();
        instance.processRequest(request, response);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doGet method, of class Controller.
     */
    @Test
    public void testDoGet() throws Exception {
        System.out.println("doGet");
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        Controller instance = new Controller();
        instance.doGet(request, response);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doPost method, of class Controller.
     */
    @Test
    public void testDoPost() throws Exception {
        System.out.println("doPost");
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        Controller instance = new Controller();
        instance.doPost(request, response);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    // Register
    // Integration test (i.e., register + Authentication.register)
    @Test
    public void testRegister() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Controller controller = new Controller();
        Method method = Controller.class.getDeclaredMethod("register", HttpServletRequest.class);
        method.setAccessible(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("username")).thenReturn("testUser");
        when(request.getParameter("password")).thenReturn("123");
        boolean successful = (boolean)method.invoke(controller, request);
        assertEquals(true, successful);
    }
    
    // Login
    // Integration test (i.e., login + Authentication.authenticate)
    @Test
    public void testLogin() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Controller controller = new Controller();
        Method method = Controller.class.getDeclaredMethod("login", HttpServletRequest.class);
        method.setAccessible(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getParameter("username")).thenReturn("testUser");
        when(request.getParameter("password")).thenReturn("123");
        when(request.getSession()).thenReturn(session);
        boolean successful = (boolean)method.invoke(controller, request);
        assertEquals(true, successful);
    }
    
    //upload
    // Create form list
    @Test
    public void testCreateFormList() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Controller controller = new Controller();
        Method method = Controller.class.getDeclaredMethod("createFormList");
        method.setAccessible(true);
        ArrayList<PDFForm> formList = (ArrayList<PDFForm>)method.invoke(controller);
        assertEquals(true, !formList.isEmpty());
    }
}
