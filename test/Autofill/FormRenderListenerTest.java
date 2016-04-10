package Autofill;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.LineSegment;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormRenderListenerTest {
    
    public FormRenderListenerTest() {
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

    // Test reset
    public void testReset() {
        ArrayList<FormText> formText;
        FormRenderListener listener = new FormRenderListener();
        formText = listener.getFormText();
        formText.add(new FormText(""));
        listener.reset();
        assertEquals(0, formText.size());
    }
    
    // Test renderText
    @Test
    public void testRenderText() {
        LineSegment ascentLine = new LineSegment(new Vector(20, 20, 0), new Vector(40, 20, 0));
        LineSegment descentLine = new LineSegment(new Vector(20, 10, 0), new Vector(40, 10, 0));
        TextRenderInfo renderInfo = mock(TextRenderInfo.class);
        when(renderInfo.getAscentLine()).thenReturn(ascentLine);
        when(renderInfo.getDescentLine()).thenReturn(descentLine);
        when(renderInfo.getText()).thenReturn("Text ");
        FormRenderListener listener = new FormRenderListener();
        listener.renderText(renderInfo);
        ArrayList<FormText> formTexts = listener.getFormText();
        String resultText = formTexts.get(0).getName();
        Rectangle resultPosition = formTexts.get(0).getPosition();
        String expectedText = "Text";
        Rectangle expectedPosition = new Rectangle(20, 10, 40, 20);
        assertEquals(expectedText, resultText);
        assertEquals(expectedPosition, resultPosition);
    }
}
