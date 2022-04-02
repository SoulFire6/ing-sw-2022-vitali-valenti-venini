package it.polimi.softeng.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameTest {
    @Test
    public void testGameConstructor() {
        String testID="test";
        Integer testPlayerNum=2;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            playerNames.add("Player_"+i+1);
        }
        ArrayList<Player> testPlayers=Team.genTeams(playerNames);
        Integer testBagFill=26;
        Integer testCloudNum=2;
        Integer testCloudMax=3;
        Integer testIslandNum=12;
        Game game=new Game(testID,testPlayers,testBagFill,testCloudNum,testCloudMax,testIslandNum);
        assertEquals(testID,game.getGameID());
        assertEquals(testPlayers,game.getPlayers());
        assertEquals(testPlayerNum,game.getPlayerNum());
        assertEquals(testBagFill*Colour.values().length,game.getBag().getFillAmount().intValue());
        assertEquals(testCloudNum.intValue(),game.getClouds().size());
        for (Cloud_Tile cloud: game.getClouds()) {
            assertEquals(testCloudMax,cloud.getMaxSlots());
        }
        assertEquals(testIslandNum.intValue(),game.getIslands().size());
        assertEquals(false,game.isExpertMode());
        assertNull(game.getCoins());
        assertNull(game.getCharacterCards());

    }
    @Test
    public void testExpertGameConstructor() {
        Integer testPlayerNum=2;
        ArrayList<String> expertPlayerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            expertPlayerNames.add("Player_"+i+1);
        }
        ArrayList<Player> expertPlayers=Team.genTeams(expertPlayerNames);
        Integer testCoins=20;
        Integer testCharCardNum=3;
        Game game=new Game("test_expert",expertPlayers,26,2,3,12,testCoins,testCharCardNum);
        assertEquals(testCoins,game.getCoins());
        assertEquals(testCharCardNum.intValue(),game.getCharacterCards().size());
        assertEquals(true,game.isExpertMode());
    }
    @Test
    public void testSetCoins() {
        Integer initialCoins=20;
        Integer newCoins=10;
        ArrayList<String> playerNames=new ArrayList<>(Arrays.asList("Player_1","Player_2"));
        Game game=new Game("test_coins",Team.genTeams(playerNames),26,2,3,12,initialCoins,3);
        assertEquals(initialCoins,game.getCoins());
        game.setCoins(newCoins);
        assertEquals(newCoins,game.getCoins());
    }
    @Test
    public void testGetTeams() {
        Integer teamNum;
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
            assertEquals(teamNum.intValue(),game.getTeams().size());
        }
    }
}
