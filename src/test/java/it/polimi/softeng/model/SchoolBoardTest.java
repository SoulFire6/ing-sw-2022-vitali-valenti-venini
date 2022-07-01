package it.polimi.softeng.model;

import it.polimi.softeng.controller.AssistantCardController;

import java.util.ArrayList;
import java.util.EnumMap;

import it.polimi.softeng.exceptions.MoveNotAllowedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SchoolBoard_Tile
 */
public class SchoolBoardTest {
    /**
     * Tests default constructor
     */
    @Test
    public void testSchoolBoardConstructor() {
        AssistantCardController assistantCardController=new AssistantCardController();
        String testPlayerName="test";
        int testMaxEntranceSlots=7;
        int testTowers=8;
        int testMaxTowers=8;
        ArrayList<AssistantCard> testHand=assistantCardController.genHand();
        int testCoins=0;
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile(testPlayerName,testMaxEntranceSlots,testTowers,testMaxTowers,testHand,testCoins);
        assertTrue(schoolBoard.getTileID().contains(testPlayerName));
        assertEquals(testTowers,schoolBoard.getTowers());
        assertEquals(testMaxTowers,schoolBoard.getMaxTowers());
        assertEquals(testCoins,schoolBoard.getCoins());
    }
    /**
     * Tests filling entrance from bag
     */
    @Test
    public void testFillEntranceFromBag() {
        Bag_Tile bag=new Bag_Tile(10);
        int testMaxEntrance=7;
        int bagFill=bag.getFillAmount();
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",testMaxEntrance,0,0,null,0);
        schoolBoard.fillEntrance(bag);
        assertEquals(testMaxEntrance,schoolBoard.getFillAmount());
        assertEquals(bagFill-testMaxEntrance,bag.getFillAmount());
    }
    /**
     * Tests filling entrance from cloud
     */
    @Test
    public void testFillEntranceFromCloud() {
        int testFillNum=3;
        int testMaxEntranceSlots=7;
        Bag_Tile bag=new Bag_Tile(10);
        Cloud_Tile cloud=new Cloud_Tile("testCloud",testFillNum);
        cloud.fillCloud(bag);
        SchoolBoard_Tile schoolBoard = new SchoolBoard_Tile("test",testMaxEntranceSlots,0,0,null,0);
        schoolBoard.fillEntrance(bag);
        while (schoolBoard.getFillAmount()>(testMaxEntranceSlots-testFillNum)) {
            schoolBoard.removeColour(Colour.getRandomColour(),schoolBoard.getFillAmount()-(testMaxEntranceSlots-testFillNum));
        }
        assertEquals(testMaxEntranceSlots-testFillNum,schoolBoard.getFillAmount());
        assertDoesNotThrow(()->schoolBoard.fillEntrance(cloud));
        cloud.fillCloud(new Bag_Tile(10));
        assertThrows(MoveNotAllowedException.class,()->schoolBoard.fillEntrance(cloud));
        assertEquals(testMaxEntranceSlots,schoolBoard.getFillAmount());
    }

    /**
     * Tests setter and getter of dining room
     */
    @Test
    public void testDiningRoom() {
        SchoolBoard_Tile schoolBoard_tile=new SchoolBoard_Tile("test",0,0,0,null,0);
        Bag_Tile bag=new Bag_Tile(2);
        schoolBoard_tile.setDiningRoom(bag.getContents());
        assertEquals(bag.getContents(),schoolBoard_tile.getDiningRoom());
    }

    /**
     * Tests getter for professor table
     */
    @Test
    public void testGetProfessorTable() {
        SchoolBoard_Tile schoolBoard_tile=new SchoolBoard_Tile("test",0,0,0,null,0);
        EnumMap<Colour,Boolean> professorTable=Colour.genBooleanMap();
        for (Colour c : Colour.values()) {
            schoolBoard_tile.setProfessor(c,c.ordinal()%2==0);
            professorTable.put(c,c.ordinal()%2==0);
        }
        assertEquals(professorTable,schoolBoard_tile.getProfessorTable());
    }
    /**
     * Tests getter for dining room amount
     */
    @Test
    public void testGetDiningRoomAmount() {
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",7,0,0,null,0);
        for (Colour c: Colour.values()) {
            assertEquals(0,schoolBoard.getDiningRoomAmount(c));
        }
    }
    /**
     * Tests adding students to dining room
     */
    @Test
    public void testMoveStudentsToDiningRoom() {
        Colour randColour=Colour.getRandomColour();
        Bag_Tile bag=new Bag_Tile(0);
        bag.addColour(randColour,20);
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",10,0,0,null,0);
        for (int i=0; i<10; i++) {
            assertTrue(schoolBoard.moveStudentToDiningRoom(randColour));
            assertEquals(i+1,schoolBoard.getDiningRoomAmount(randColour));
        }
        assertFalse(schoolBoard.moveStudentToDiningRoom(randColour));
    }
    /**
     * Tests moving students to island
     */
    @Test
    public void testMoveStudentToIsland() {
        int testMoveAmount=7;
        Colour randColour=Colour.getRandomColour();
        Bag_Tile bag=new Bag_Tile(0);
        bag.addColour(randColour,testMoveAmount);
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",testMoveAmount,0,0,null,0);
        schoolBoard.fillEntrance(bag);
        Island_Tile island=new Island_Tile("testIsland");
        for (int i=0; i<testMoveAmount; i++) {
            assertTrue(schoolBoard.moveStudentToIsland(randColour,island));
        }
        int testIslandAmount=island.getFillAmount();
        schoolBoard.moveStudentToIsland(randColour,island);
        assertEquals(testIslandAmount,island.getFillAmount());
    }
    /**
     * Tests professorTable setters and getters
     */
    @Test
    public void testProfessors() {
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,0,null,0);
        Colour randColour=Colour.getRandomColour();
        for (Colour c: Colour.values()) {
            assertFalse(schoolBoard.getProfessor(c));
        }
        schoolBoard.setProfessor(randColour,true);
        assertTrue(schoolBoard.getProfessor(randColour));
    }
    /**
     * Tests modifying tower num
     */
    @Test
    public void testModifyTowers() {
        int testTowers=8;
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,testTowers,null,0);
        assertEquals(0,schoolBoard.getTowers());
        assertTrue(schoolBoard.modifyTowers(testTowers));
        assertEquals(testTowers,schoolBoard.getTowers());
        assertFalse(schoolBoard.modifyTowers(1));
    }
    /**
     * Tests setter and getter for coins
     */
    @Test
    public void testCoins() {
        int initCoins=0;
        int testCoins=10;
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,0,null,initCoins);
        assertEquals(initCoins,schoolBoard.getCoins());
        schoolBoard.setCoins(testCoins);
        assertEquals(testCoins,schoolBoard.getCoins());
    }
    /**
     * Tests playing an assist card
     */
    @Test
    public void testPlayAssistantCard() {
        AssistantCardController assistantCardController=new AssistantCardController();
        ArrayList<AssistantCard> hand=assistantCardController.genHand();
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,0,hand,0);
        AssistantCard testCard;
        int currentHandSize=schoolBoard.getHand().size();
        while (currentHandSize>0) {
            assertEquals(currentHandSize--,schoolBoard.getHand().size());
            testCard=schoolBoard.getHand().get(0);
            schoolBoard.playAssistantCard(testCard.getCardID());
            assertEquals(testCard,schoolBoard.getLastUsedCard());
        }
    }
}
