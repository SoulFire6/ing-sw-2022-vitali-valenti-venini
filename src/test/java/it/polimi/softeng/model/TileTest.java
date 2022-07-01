package it.polimi.softeng.model;

import java.util.EnumMap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for Tile
 */
public class TileTest {
    /**
     * Class implementation for tests
     */
    private static class Test_Tile extends Tile{
        private Test_Tile() {
            super();
        }
        private Test_Tile(int num) {
            super(num);
        }
    }
    /**
     * Tests default constructor
     */
    @Test
    public void testTileBaseConstructor() {
        Tile tile=new Test_Tile();
        assertEquals(0,tile.getFillAmount());
    }
    /**
     * Tests constructor with passed int argument
     */
    @Test
    public void testTileConstructorWithNum() {
        int num=5;
        Tile tile=new Test_Tile(num);
        assertEquals(num*Colour.values().length,tile.getFillAmount());
    }
    /**
     * Tests getter for id
     */
    @Test
    public void testTileID() {
        String testID="test";
        Tile tile=new Test_Tile();
        tile.setTileID(testID);
        assertEquals(testID,tile.getTileID());
    }
    /**
     * Tests adding int to contents of a certain colours
     */
    @Test
    public void testAddColour() {
        int num=10;
        Colour randColour=Colour.getRandomColour();
        Tile tile=new Test_Tile();
        tile.addColour(randColour,num);
        for (Colour c: Colour.values()) {
            if (c==randColour) {
                assertEquals(num,tile.getContents().get(c).intValue());
            } else {
                assertEquals(0,tile.getContents().get(c).intValue());
            }
        }
    }
    /**
     * Tests removing a disk
     */
    @Test
    public void testRemoveColour() {
        int num=5;
        int secondNum=3;
        Colour randColour=Colour.getRandomColour();
        Tile tile=new Test_Tile(num);
        assertEquals(true,tile.removeColour(randColour,secondNum));
        for (Colour c: Colour.values()) {
            if (c==randColour) {
                assertEquals(num-secondNum,tile.getContents().get(c).intValue());
            } else {
                assertEquals(num,tile.getContents().get(c).intValue());
            }
        }
        assertEquals(false,tile.removeColour(randColour,10));
        assertNull(tile.removeColour(randColour,-1));
    }
    /**
     * Tests removing more disks than available
     */
    @Test
    public void testRemoveColourWithValueHigherThanCurrent() {
        int num=5;
        Colour randColour=Colour.getRandomColour();
        Tile tile=new Test_Tile(num);
        tile.removeColour(randColour,num+1);
        for (Colour c: Colour.values()) {
            if (c==randColour) {
                assertEquals(0,tile.getContents().get(c).intValue());
            } else {
                assertEquals(num,tile.getContents().get(c).intValue());
            }
        }
    }
    /**
     * Tests getter and setter for content
     */
    @Test
    public void testContents() {
        int idx=10;
        Tile tile=new Test_Tile();
        EnumMap<Colour, Integer> testContents=Colour.genIntegerMap();
        for (Colour c: Colour.values()) {
            testContents.put(c,idx--);
        }
        tile.setContents(testContents);
        for (Colour c: Colour.values()) {
            assertEquals(testContents.get(c),tile.getContents().get(c));
        }
    }
    /**
     * Tests adding disks with int
     */
    @Test
    public void testAddStudentsWithNum() {
        int initialNum=10;
        int addedNum=5;
        Tile tile=new Test_Tile(initialNum);
        tile.addStudents(addedNum);
        for (Colour c: Colour.values()) {
            assertEquals(initialNum+addedNum,tile.getContents().get(c).intValue());
        }
    }
    /**
     * Tests adding disks with EnumMap
     */
    @Test
    public void testAddStudentsWithEnumMap() {
        int idx=10;
        int initialNum=5;
        EnumMap<Colour,Integer> testStudents=Colour.genIntegerMap();
        Tile tile=new Test_Tile(initialNum);
        for (Colour c: Colour.values()) {
            testStudents.put(c,idx--);
        }
        tile.addStudents(testStudents);
        for (Colour c: Colour.values()) {
            assertEquals(testStudents.get(c)+initialNum,tile.getContents().get(c).intValue());
        }
    }
    /**
     * Tests clearing tile contents
     */
    @Test
    public void testEmptyTile() {
        Tile tile=new Test_Tile(10);
        tile.emptyTile();
        for (Colour c: Colour.values()) {
            assertEquals(0,(int)tile.getContents().get(c));
        }
    }
    /**
     * Tests empty tile
     */
    @Test
    public void testIsEmpty() {
        Tile tile=new Test_Tile();
        assertTrue(tile.isEmpty());
        tile.addColour(Colour.getRandomColour(),1);
        assertFalse(tile.isEmpty());
    }
}
