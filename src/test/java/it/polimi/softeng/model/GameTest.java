package it.polimi.softeng.model;

import it.polimi.softeng.controller.CharCardController;
import it.polimi.softeng.controller.PlayerController;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameTest {
    @Test
    public void testGameConstructor() {
        /*
        String testID="test";
        int testPlayerNum=2;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            playerNames.add("Player_"+i+1);
        }
        ArrayList<Player> testPlayers= PlayerController.genTeams(playerNames);
        int testBagFill=26;
        int testCloudNum=2;
        int testCloudMax=3;
        int testIslandNum=12;
        Game game=new Game(testID,testPlayers,testBagFill,testCloudNum,testCloudMax,testIslandNum);
        assertEquals(testID,game.getGameID());
        assertEquals(testPlayers,game.getPlayers());
        assertEquals(testPlayerNum,game.getPlayerNum());
        //deprecated since bag gets changed in constructor if islands are generated
        //assertEquals(testBagFill*Colour.values().length,game.getBag().getFillAmount());
        assertEquals(testCloudNum,game.getClouds().size());
        for (Cloud_Tile cloud: game.getClouds()) {
            assertEquals(testCloudMax,cloud.getMaxSlots());
        }
        assertEquals(testIslandNum,game.getIslands().size());
        assertEquals(false,game.isExpertMode());
        assertEquals(-1,game.getCoins());
        assertNull(game.getCharacterCards());

        TODO: update test
         */
    }
    @Test
    public void testSetCoins() {
        PlayerController playerController=new PlayerController();
        int initialCoins=20;
        int newCoins=10;
        Game game=new Game("test_coins",null,null,null,null,null,false,initialCoins,null);
        assertEquals(initialCoins,game.getCoins());
        game.setCoins(newCoins);
        assertEquals(newCoins,game.getCoins());
    }
}
