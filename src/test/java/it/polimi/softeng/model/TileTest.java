package it.polimi.softeng.model;

import org.junit.Test;

import java.util.EnumMap;

import static org.junit.Assert.*;

public class TileTest {
    //Class implementation for tests only
    private static class Test_Tile extends Tile{
        private Test_Tile() {
            super();
        }
        private Test_Tile(int num) {
            super(num);
        }
    }

    @Test
    public void testTileBaseConstructor() {
        Tile tile=new Test_Tile();
        assertEquals(0,tile.getFillAmount());
    }
    @Test
    public void testTileConstructorWithNum() {
        int num=5;
        Tile tile=new Test_Tile(num);
        assertEquals(num*Colour.values().length,tile.getFillAmount());
    }
    @Test
    public void testTileID() {
        String testID="test";
        Tile tile=new Test_Tile();
        tile.setTileID(testID);
        assertEquals(testID,tile.getTileID());
    }
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
    @Test
    public void testRemoveColour() {
        int num=5;
        int secondNum=3;
        Colour randColour=Colour.getRandomColour();
        Tile tile=new Test_Tile(num);
        tile.removeColour(randColour,secondNum);
        for (Colour c: Colour.values()) {
            if (c==randColour) {
                assertEquals(num-secondNum,tile.getContents().get(c).intValue());
            } else {
                assertEquals(num,tile.getContents().get(c).intValue());
            }
        }
    }
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
    @Test
    public void testContents() {
        int idx=10;
        Tile tile=new Test_Tile();
        EnumMap<Colour, Integer> testContents=Colour.genStudentMap();
        for (Colour c: Colour.values()) {
            testContents.put(c,idx--);
        }
        tile.setContents(testContents);
        for (Colour c: Colour.values()) {
            assertEquals(testContents.get(c),tile.getContents().get(c));
        }
    }
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
    @Test
    public void testAddStudentsWithEnumMap() {
        int idx=10;
        int initialNum=5;
        EnumMap<Colour,Integer> testStudents=Colour.genStudentMap();
        Tile tile=new Test_Tile(initialNum);
        for (Colour c: Colour.values()) {
            testStudents.put(c,idx--);
        }
        tile.addStudents(testStudents);
        for (Colour c: Colour.values()) {
            assertEquals(testStudents.get(c)+initialNum,tile.getContents().get(c).intValue());
        }
    }
    @Test
    public void testEmptyTile() {
        Tile tile=new Test_Tile(10);
        tile.emptyTile();
        for (Colour c: Colour.values()) {
            assertEquals(0,(int)tile.getContents().get(c));
        }
    }
    @Test
    public void testIsEmpty() {
        Tile tile=new Test_Tile();
        assertTrue(tile.isEmpty());
        tile.addColour(Colour.getRandomColour(),1);
        assertFalse(tile.isEmpty());
    }
}
