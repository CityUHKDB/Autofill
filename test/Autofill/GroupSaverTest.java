package Autofill;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletInputStream;
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

public class GroupSaverTest {
    
    public GroupSaverTest() {
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

    // User is stored in session
    @Test
    public void testProcessRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        User user = new User();
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        String group = "{\"name\": \"test\"}";
        final ByteArrayInputStream is = new ByteArrayInputStream(group.getBytes());
        when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return is.read();
            }
        });
        GroupSaver groupSaver = new GroupSaver();
        groupSaver.processRequest(request, response);
        assertEquals(group, user.getGroup());
    }

    // Integration test (processRequest & doGet)
    // User is stored in session
    @Test
    public void testDoGet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        User user = new User();
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        String group = "{\"name\": \"test\"}";
        final ByteArrayInputStream is = new ByteArrayInputStream(group.getBytes());
        when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return is.read();
            }
        });
        GroupSaver groupSaver = new GroupSaver();
        groupSaver.doGet(request, response);
        assertEquals(group, user.getGroup());
    }

    // Integration test (processRequest & doPost)
    // User is stored in session
    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        User user = new User();
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        String group = "{\"name\": \"test\"}";
        final ByteArrayInputStream is = new ByteArrayInputStream(group.getBytes());
        when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return is.read();
            }
        });
        GroupSaver groupSaver = new GroupSaver();
        groupSaver.doPost(request, response);
        assertEquals(group, user.getGroup());
    }

}
