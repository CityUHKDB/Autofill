package Autofill;

import com.itextpdf.text.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class StructureAnalyserTest {
    
    public StructureAnalyserTest() {
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

    // Is on the right head side of another field
    @Test
    public void testIsOnRightTrue() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isOnRight", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangleRight = new Rectangle(30, 10, 40, 20);
        Rectangle rectangleLeft = new Rectangle(10, 10, 20, 20);
        boolean isOnRight = (boolean)method.invoke(structureAnalyser, rectangleRight, rectangleLeft);
        assertEquals(true, isOnRight);
    }
    
    // Is not on the right head side of another field
    @Test
    public void testIsOnRightFalse() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isOnRight", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangleRight = new Rectangle(30, 10, 40, 20);
        Rectangle rectangleLeft = new Rectangle(10, 10, 20, 20);
        boolean isOnRight = (boolean)method.invoke(structureAnalyser, rectangleLeft, rectangleRight);
        assertEquals(false, isOnRight);
    }
    
    // Is on the same line
    @Test
    public void testIsOnSameLineTrue() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isOnSameLine", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(30, 10, 40, 20);
        Rectangle rectangle2 = new Rectangle(10, 10, 20, 20);
        boolean isOnSameLine = (boolean)method.invoke(structureAnalyser, rectangle1, rectangle2);
        assertEquals(true, isOnSameLine);
    }
    
    // Is not on the same line
    @Test
    public void testIsOnSameLineFalse() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isOnSameLine", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(10, 40, 20, 50);
        Rectangle rectangle2 = new Rectangle(10, 10, 20, 20);
        boolean isOnSameLine = (boolean)method.invoke(structureAnalyser, rectangle1, rectangle2);
        assertEquals(false, isOnSameLine);
    }
    
    // Is separated
    @Test
    public void testIsSeperatedTrue() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isSeperated", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangleUpper = new Rectangle(10, 40, 20, 50);
        Rectangle rectangleLower = new Rectangle(10, 10, 20, 20);
        boolean isSeperated = (boolean)method.invoke(structureAnalyser, rectangleLower, rectangleUpper);
        assertEquals(true, isSeperated);
    }   
    
    // Is not separated
    @Test
    public void testIsSeperatedFalse() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isSeperated", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangleUpper = new Rectangle(10, 20, 20, 30);
        Rectangle rectangleLower = new Rectangle(10, 10, 20, 20);
        boolean isSeperated = (boolean)method.invoke(structureAnalyser, rectangleLower, rectangleUpper);
        assertEquals(false, isSeperated);
    } 
    
    // Is new gruop (ie, TT)
    @Test
    public void testIsNewSectionTrue() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isNewSection", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(10, 10, 20, 20);
        Rectangle rectangle2 = new Rectangle(30, 40, 40, 50);
        boolean isNewSection = (boolean)method.invoke(structureAnalyser, rectangle1, rectangle2);
        assertEquals(true, isNewSection);
    }
    
    
    // Is not new group (ie, FF)
    @Test
    public void testIsNewSectionFalse() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isNewSection", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(30, 40, 40, 50);
        Rectangle rectangle2 = new Rectangle(10, 10, 20, 20);
        boolean isNewSection = (boolean)method.invoke(structureAnalyser, rectangle1, rectangle2);
        assertEquals(false, isNewSection);
    }
    
    // Is same line previous field (ie, TT)
    @Test
    public void testIsSameLinePreviousFieldTrue() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isSameLinePreviousField", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(30, 10, 40, 20);
        Rectangle rectangle2 = new Rectangle(10, 10, 20, 20);
        boolean isSameLinePreviousField = (boolean)method.invoke(structureAnalyser, rectangle1, rectangle2);
        assertEquals(true, isSameLinePreviousField);
    }
    
    // Is not same line previous field (ie, FF)
    @Test
    public void testIsSameLinePreviousFieldFalse() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StructureAnalyser structureAnalyser = StructureAnalyser.getInstance();
        Method method = StructureAnalyser.class.getDeclaredMethod("isSameLinePreviousField", Rectangle.class, Rectangle.class);
        method.setAccessible(true);
        Rectangle rectangle1 = new Rectangle(10, 10, 20, 20);
        Rectangle rectangle2 = new Rectangle(30, 10, 40, 20);
        boolean isSameLinePreviousField = (boolean)method.invoke(structureAnalyser, rectangle1, rectangle2);
        assertEquals(false, isSameLinePreviousField);
    }   
    
    // Test analyse
}
