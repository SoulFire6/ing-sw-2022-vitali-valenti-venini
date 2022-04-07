package it.polimi.softeng.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameTest {
    @Test
    public void testGameConstructor() {
        String testID="test";
        int testPlayerNum=2;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            playerNames.add("Player_"+i+1);
        }
        ArrayList<Player> testPlayers=Team.genTeams(playerNames);
        int testBagFill=26;
        int testCloudNum=2;
        int testCloudMax=3;
        int testIslandNum=12;
        Game game=new Game(testID,testPlayers,testBagFill,testCloudNum,testCloudMax,testIslandNum);
        assertEquals(testID,game.getGameID());
        assertEquals(testPlayers,game.getPlayers());
        assertEquals(testPlayerNum,game.getPlayerNum());
        assertEquals(testBagFill*Colour.values().length,game.getBag().getFillAmount());
        assertEquals(testCloudNum,game.getClouds().size());
        for (Cloud_Tile cloud: game.getClouds()) {
            assertEquals(testCloudMax,cloud.getMaxSlots());
        }
        assertEquals(testIslandNum,game.getIslands().size());
        assertEquals(false,game.isExpertMode());
        assertEquals(-1,game.getCoins());
        assertNull(game.getCharacterCards());

    }
    @Test
    public void testExpertGameConstructor() {
        int testPlayerNum=2;
        ArrayList<String> expertPlayerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            expertPlayerNames.add("Player_"+i+1);
        }
        ArrayList<Player> expertPlayers=Team.genTeams(expertPlayerNames);
        int testCoins=20;
        int testCharCardNum=3;
        Game game=new Game("test_expert",expertPlayers,26,2,3,12,testCoins,testCharCardNum);
        assertEquals(testCoins,game.getCoins());
        assertEquals(testCharCardNum,game.getCharacterCards().size());
        assertEquals(true,game.isExpertMode());
    }
    @Test
    public void testSetCoins() {
        int initialCoins=20;
        int newCoins=10;
        ArrayList<String> playerNames=new ArrayList<>(Arrays.asList("Player_1","Player_2"));
        Game game=new Game("test_coins",Team.genTeams(playerNames),26,2,3,12,initialCoins,3);
        assertEquals(initialCoins,game.getCoins());
        game.setCoins(newCoins);
        assertEquals(newCoins,game.getCoins());
    }
    @Test
    public void testGetTeams() {
        int teamNum;
        for (int i=2; i<5; i++) {
            ArrayList<String> playerNames=new ArrayList<>();
            for (int j=0; j<i; j++) {
                playerNames.add("Player_"+j+1);
            }
            Game game=new Game("test_teams_"+i,Team.genTeams(playerNames),26,2,3,12);
            switch (i) {
                case 2:
                    case 4:
                    teamNum=2;
                    break;
                case 3:
                    teamNum=3;
                    break;
                default:
                    teamNum=0;
                    break;
            }
            assertEquals(teamNum,game.getTeams().size());
        }
    }
}
