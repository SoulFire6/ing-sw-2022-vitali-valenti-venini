package it.polimi.softeng.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.EnumMap;

/**
 * Test class for Colour
 */
public class ColourTest {
    /**
     * Tests generating Colour EnumMap of Integer
     */
    @Test
    public void testGenStudentMap() {
        EnumMap<Colour,Integer> testMap=Colour.genIntegerMap();
        for (Colour c: Colour.values()) {
            assertEquals(0,testMap.get(c).intValue());
        }
    }
    /**
     * Tests generating Colour EnumMap of Boolean
     */
    @Test
    public void testGenProfessorMap() {
        EnumMap<Colour,Boolean> testMap=Colour.genBooleanMap();
        for (Colour c: Colour.values()) {
            assertEquals(false,testMap.get(c));
        }
    }
    /**
     * Tests parsing Colour values
     */
    @Test
    public void testParseChosenColour() {
        String[] testColours={"yElLOw","BLUE","green","rEd","pURpLe"};
        Colour[] correctColours=Colour.values();
        for (int i=0; i<testColours.length; i++) {
            assertEquals(correctColours[i],Colour.parseChosenColour(testColours[i]));
        }
    }
    /**
     * Tests parsing incorrect Colour value
     */
    @Test
    public void testParseChosenColourWithValueOutsideEnum() {
        String notAColour="test";
        assertNull(Colour.parseChosenColour(notAColour));
    }
    /**
     * Tests generating random Colour
     */
    @Test
    public void testGetRandomColour() {
        Colour randColour=Colour.getRandomColour();
        assertTrue(Arrays.asList(Colour.values()).contains(randColour));
    }
}
