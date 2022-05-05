package it.polimi.softeng.model;

import it.polimi.softeng.controller.PlayerController;
import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameTest {
    @Test
    public void testGameConstructor() {
        String testID="Game";
        PlayerController playerController=new PlayerController();
        ArrayList<CharacterCard> cards=new ArrayList<>();
        ArrayList<String> playerNames=new ArrayList<>();
        playerNames.add("test1");
        playerNames.add("test2");
        Bag_Tile bag=new Bag_Tile(26);
        ArrayList<Player> players;
        try {
            players=playerController.genPlayers(playerNames);
        }
        catch (InvalidPlayerNumException ipne) {
            players=null;
        }
        assertNotNull(players);
        ArrayList<Team> teams = playerController.getTeams(players);
        ArrayList<Island_Tile> islands = new ArrayList<>();
        ArrayList<Cloud_Tile> clouds = new ArrayList<>();
        boolean expertModeTest=true;
        int testCoins=20;
        Game game = new Game(testID,players,teams,bag,clouds,islands,expertModeTest,testCoins,cards);
        assertEquals(testID,game.getGameID());
        assertEquals(players,game.getPlayers());
        assertEquals(teams,game.getTeams());
        assertEquals(bag,game.getBag());
        assertEquals(clouds,game.getClouds());
        assertEquals(islands,game.getIslands());
        assertEquals(expertModeTest,game.isExpertMode());
        assertEquals(testCoins,game.getCoins());
        assertEquals(cards,game.getCharacterCards());

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
