package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for TileController
 */
public class TileControllerTest {
    private static final TileController tileController=new TileController();
    /**
     * Tests island generation
     */
    @Test
    public void testGenIslands() {
        int testNum=12;
        Bag_Tile testBag=new Bag_Tile(testNum);
        ArrayList<Island_Tile> islands=tileController.genIslands(testNum,testBag);
        assertEquals(testNum, islands.size());
        //testNum-2 islands have a student disk so testNum * 5 colours - testNum + 2 = testNum*4 + 2
        assertEquals((testNum*4)+2,testBag.getFillAmount());
    }
    /**
     * Tests refilling clouds
     */
    @Test
    public void testGenAndRefillClouds() {
        int testNum=4;
        int testMax=3;
        int testBagNum=testNum*(5-testMax);
        Bag_Tile testBag=new Bag_Tile(testNum);
        ArrayList<Cloud_Tile> clouds=tileController.genClouds(testNum,testMax,testBag);
        assertEquals(testNum,clouds.size());
        for (Cloud_Tile cloud : clouds) {
            assertEquals(testMax,cloud.getMaxSlots());
        }
        assertEquals(testBagNum,testBag.getFillAmount());
        for (Cloud_Tile cloud : clouds) {
            cloud.emptyTile();
        }
        testBag.setContents(new Bag_Tile(testNum).getContents());
        assertDoesNotThrow(()->tileController.refillClouds(clouds,testBag));
        assertEquals(testBagNum,testBag.getFillAmount());
    }
    /**
     * Tests moving motherNature
     */
    @Test
    public void testMoveMotherNature() {
        PlayerController playerController=new PlayerController();
        try {
            ArrayList<Player> players=playerController.genPlayers(new ArrayList<>(Arrays.asList("test","test2")));
            for (Player player : players) {
                player.setSchoolBoard(new SchoolBoard_Tile("",0,0,0,null,0));
                player.getSchoolBoard().setLastUsedCard(new AssistantCard("",0,5));
            }
            ArrayList<Island_Tile> islands=tileController.genIslands(12,new Bag_Tile(2));
            assertDoesNotThrow(()->tileController.moveMotherNature(players.get(0),1,null,players,null,islands,playerController));
        }
        catch (InvalidPlayerNumException invalidPlayerNumException) {
            fail();
        }
    }
    /**
     * Tests moving student disk to island
     */
    @Test
    public void testMoveStudentToIsland() {
        Colour c=Colour.getRandomColour();
        EnumMap<Colour,Integer> entrance=new EnumMap<>(Colour.class);
        entrance.put(c,10);
        Player testPlayer=new Player("test", Team.WHITE);
        testPlayer.setSchoolBoard(new SchoolBoard_Tile(null,0,0,0,null,0));
        String islandID="island";
        ArrayList<Island_Tile> islands=new ArrayList<>();
        islands.add(new Island_Tile("test"));
        islands.add(new Island_Tile(islandID));
        assertThrows(TileNotFoundException.class,()->tileController.moveStudentsToIsland(testPlayer,c,"illegal id",islands));
        assertThrows(InsufficientResourceException.class,()->tileController.moveStudentsToIsland(testPlayer,c,islandID,islands));
        testPlayer.getSchoolBoard().setContents(entrance);
        assertDoesNotThrow(()->tileController.moveStudentsToIsland(testPlayer,c,islandID,islands));
    }
    /**
     * Tests refilling entrance from cloud
     */
    @Test
    public void testRefillEntranceFromCloud() {
        int testNum=4;
        ArrayList<Cloud_Tile> clouds=tileController.genClouds(1,testNum,new Bag_Tile(2));
        String cloudID=clouds.get(0).getTileID();
        Player testPlayer=new Player("test",null);
        testPlayer.setSchoolBoard(new SchoolBoard_Tile(null,testNum,0,0,null,0));
        assertThrows(TileNotFoundException.class,()->tileController.refillEntranceFromCloud(testPlayer,"illegal id",clouds));
        assertDoesNotThrow(()->tileController.refillEntranceFromCloud(testPlayer,cloudID,clouds));
        assertEquals(testNum,testPlayer.getSchoolBoard().getFillAmount());
        assertThrows(TileEmptyException.class,()->tileController.refillEntranceFromCloud(testPlayer,cloudID,clouds));
    }
    /**
     * Tests calculating influence
     */
    @Test
    public void testInfluence() {
        PlayerController playerController=new PlayerController();
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<2; i++) {
            playerNames.add("Player_"+(i+1));
        }
        ArrayList<Player> players=null;
        try {
            players=playerController.genPlayers(playerNames);
            for (Player p : players) {
                p.setSchoolBoard(new SchoolBoard_Tile(null,0,0,0,null,0));
            }
        }
        catch (InvalidPlayerNumException ipne) {
            fail();
        }
        ArrayList<Island_Tile> islands=tileController.genIslands(1,new Bag_Tile(1));
        islands.get(0).setTeam(Team.GREY);
        for (Colour c : Colour.values()) {
            players.get(0).getSchoolBoard().setProfessor(c,true);
            islands.get(0).addColour(c,10);
        }
        ArrayList<Player> finalPlayers = players;
        assertDoesNotThrow(()->tileController.calculateInfluence(finalPlayers.get(0),islands.get(0), finalPlayers,null,null,playerController));
        assertNotEquals(Team.GREY,islands.get(0).getTeam());
        assertEquals(players.get(0).getTeam(),islands.get(0).getTeam());
    }
    /**
     * Tests island merging
     */
    @Test
    public void testMergeIslands() {
        int testNum=12;
        int testFirst=2;
        int testSecond=3;
        int testThird=4;
        ArrayList<Island_Tile> islands=tileController.genIslands(testNum,new Bag_Tile(5));
        for (Island_Tile island : islands) {
            island.emptyTile();
        }
        Island_Tile first=islands.get(0);
        Island_Tile second=islands.get(1);
        Island_Tile third=islands.get(2);
        first.setTeam(Team.WHITE);
        first.setTowers(testFirst);
        first.addColour(Colour.getRandomColour(),testFirst);
        second.setTeam(Team.WHITE);
        second.addColour(Colour.getRandomColour(),testSecond);
        second.setTowers(testSecond);
        third.setTeam(Team.WHITE);
        third.setTowers(testThird);
        third.addColour(Colour.getRandomColour(),testThird);
        tileController.checkAndMerge(islands,second);
        assertEquals(testNum-2,islands.size());
        assertEquals(Team.WHITE,islands.get(0).getTeam());
        assertEquals(testFirst+testSecond+testThird,islands.get(0).getTowers());
        assertEquals(testFirst+testSecond+testThird,islands.get(0).getFillAmount());
    }
}
