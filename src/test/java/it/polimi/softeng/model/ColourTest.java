package it.polimi.softeng.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.EnumMap;

import static org.junit.Assert.*;

public class ColourTest {
    @Test
    public void testGenStudentMap() {
        EnumMap<Colour,Integer> testMap=Colour.genStudentMap();
        for (Colour c: Colour.values()) {
            assertEquals(0,testMap.get(c).intValue());
        }
    }
    @Test
    public void testGenProfessorMap() {
        EnumMap<Colour,Boolean> testMap=Colour.genProfessorMap();
        for (Colour c: Colour.values()) {
            assertEquals(false,testMap.get(c));
        }
    }
    @Test
    public void testParseChosenColour() {
        String[] testColours={"yElLOw","BLUE","green","rEd","pURpLe"};
        Colour[] correctColours=Colour.values();
        for (int i=0; i<testColours.length; i++) {
            assertEquals(correctColours[i],Colour.parseChosenColour(testColours[i]));
        }
    }
    @Test
    public void testParseChosenColourWithValueOutsideEnum() {
        String notAColour="test";
        assertNull(Colour.parseChosenColour(notAColour));
    }
    @Test
    public void testGetRandomColour() {
        Colour randColour=Colour.getRandomColour();
        assertTrue(Arrays.asList(Colour.values()).contains(randColour));
    }
}
