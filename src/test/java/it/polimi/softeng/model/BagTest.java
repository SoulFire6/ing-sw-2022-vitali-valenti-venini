package it.polimi.softeng.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;

public class BagTest {
    @Test
    public void testBagConstructor() {
        Integer initValue=0;
        Bag_Tile b=new Bag_Tile(initValue);
        for (Colour c: Colour.values()) {
            assertEquals(initValue,b.getContents().get(c));
        }
    }
    @Test
    public void testDrawStudents() {
        EnumMap<Colour,Integer> testDraw;
        Integer testDrawNum=5;
        Integer testSum=0;
        Bag_Tile b=new Bag_Tile(testDrawNum);
        testDraw=b.drawStudents(testDrawNum);
        for (Colour c: Colour.values()) {
            testSum+=testDraw.get(c);
        }
        assertEquals(testSum,testDrawNum);
    }
    @Test
    public void testDrawStudentsWithLessThanRequired() {
        EnumMap<Colour,Integer> testDraw;
        int testDrawNum=10;
        int testSum=0;
        Bag_Tile b=new Bag_Tile(1);
        testDraw=b.drawStudents(testDrawNum);
        for (Colour c: Colour.values()) {
            testSum+=testDraw.get(c);
        }
        assertTrue(testSum<testDrawNum);
    }



}