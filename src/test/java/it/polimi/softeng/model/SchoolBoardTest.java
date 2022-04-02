package it.polimi.softeng.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class SchoolBoardTest {
    @Test
    public void testSchoolBoardConstructor() {
        String testPlayerName="test";
        Integer testMaxEntranceSlots=7;
        Integer testTowers=8;
        Integer testMaxTowers=8;
        Integer testCoins=0;
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile(testPlayerName,testMaxEntranceSlots,testTowers,testMaxTowers,testCoins);
        assertTrue(schoolBoard.getTileID().contains(testPlayerName));
        assertEquals(testMaxEntranceSlots,schoolBoard.getMaxExntranceSlots());
        assertEquals(testTowers,schoolBoard.getTowers());
        assertEquals(testMaxTowers,schoolBoard.getMaxTowers());
        assertEquals(testCoins,schoolBoard.getCoins());
    }
    @Test
    public void testFillEntranceFromBag() {
        Bag_Tile bag=new Bag_Tile(10);
        Integer testMaxEntrance=7;
        Integer bagFill=bag.getFillAmount();
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",testMaxEntrance,0,0,0);
        schoolBoard.fillEntrance(bag);
        assertEquals(testMaxEntrance,schoolBoard.getFillAmount());
        assertEquals(bagFill-testMaxEntrance,bag.getFillAmount().intValue());
    }
    @Test
    public void testFillEntranceFromCloud() {
        Integer testFillNum=3;
        Integer testMaxEntranceSlots=7;
        Integer amount;
        Bag_Tile bag=new Bag_Tile(10);
        Cloud_Tile cloud=new Cloud_Tile("testCloud",testFillNum);
        cloud.fillCloud(bag);
        SchoolBoard_Tile schoolBoard = new SchoolBoard_Tile("test",testMaxEntranceSlots,0,0,0);
        schoolBoard.fillEntrance(bag);
        while (schoolBoard.getFillAmount()>(testMaxEntranceSlots-testFillNum)) {
            schoolBoard.removeColour(Colour.getRandomColour(),schoolBoard.getFillAmount()-(testMaxEntranceSlots-testFillNum));
        }
        assertEquals(testMaxEntranceSlots-testFillNum,schoolBoard.getFillAmount().intValue());
        schoolBoard.fillEntrance(cloud);
        assertEquals(testMaxEntranceSlots,schoolBoard.getFillAmount());
    }
    @Test
    public void testGetDiningRoomAmount() {
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",7,0,0,0);
        for (Colour c: Colour.values()) {
            assertEquals(0,schoolBoard.getDiningRoomAmount(c).intValue());
        }
    }
    @Test
    public void testMoveStudentsToDiningRoom() {
        Colour randColour=Colour.getRandomColour();
        Bag_Tile bag=new Bag_Tile(0);
        bag.addColour(randColour,20);
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",10,0,0,0);
        for (int i=0; i<10; i++) {
            assertTrue(schoolBoard.moveStudentToDiningRoom(randColour));
            assertEquals(i+1,schoolBoard.getDiningRoomAmount(randColour).intValue());
        }
        assertFalse(schoolBoard.moveStudentToDiningRoom(randColour));
    }
    @Test
    public void testMoveStudentToIsland() {
        Integer testMoveAmount=7;
        Colour randColour=Colour.getRandomColour();
        Bag_Tile bag=new Bag_Tile(0);
        bag.addColour(randColour,testMoveAmount);
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",testMoveAmount,0,0,0);
        schoolBoard.fillEntrance(bag);
        Island_Tile island=new Island_Tile("testIsland");
        for (int i=0; i<testMoveAmount; i++) {
            assertTrue(schoolBoard.moveStudentToIsland(randColour,island));
        }
        Integer testIslandAmount=island.getFillAmount();
        schoolBoard.moveStudentToIsland(randColour,island);
        assertEquals(testIslandAmount,island.getFillAmount());
    }
    @Test
    public void testProfessors() {
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,0,0);
        Colour randColour=Colour.getRandomColour();
        for (Colour c: Colour.values()) {
            assertEquals(false,schoolBoard.getProfessor(c));
        }
        schoolBoard.setProfessor(randColour,true);
        assertEquals(true,schoolBoard.getProfessor(randColour));
    }
    @Test
    public void testModifyTowers() {
        Integer testTowers=8;
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,testTowers,0);
        assertEquals(0,schoolBoard.getTowers().intValue());
        assertTrue(schoolBoard.modifyTowers(testTowers));
        assertEquals(testTowers,schoolBoard.getTowers());
        assertFalse(schoolBoard.modifyTowers(1));
    }
    @Test
    public void testCoins() {
        Integer initCoins=0;
        Integer testCoins=10;
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,0,initCoins);
        assertEquals(initCoins,schoolBoard.getCoins());
        schoolBoard.setCoins(testCoins);
        assertEquals(testCoins,schoolBoard.getCoins());
    }
    @Test
    public void testPlayAssistantCard() {
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile("test",0,0,0,0);
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
