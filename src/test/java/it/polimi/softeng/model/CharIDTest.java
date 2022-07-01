package it.polimi.softeng.model;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import it.polimi.softeng.exceptions.MoveNotAllowedException;
import it.polimi.softeng.network.server.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class CharIDTest {
    LobbyController testcontroller;
    @BeforeEach
    void setup(){
        ArrayList<String> playerNames=new ArrayList<>();
        playerNames.add("test");
        playerNames.add("tester");
        String name="test";
        String name2="test2";
        try {
            testcontroller = new LobbyController(playerNames,true,"TestLobby",null);
        } catch (InvalidPlayerNumException e) {
            e.printStackTrace();
            fail();

        }

    }

    /** Test Activation of Monk*/
    @Test
    void testActivateMonk(){
        CharID monk = CharID.MONK;
        assertThrows(MoveNotAllowedException.class,()-> monk.activateCard(null,new String[]{},null));
        assertThrows(MoveNotAllowedException.class,()-> monk.activateCard(null,new String[]{},testcontroller));
        assertThrows(MoveNotAllowedException.class,()-> monk.activateCard(null,new String[]{"test","test"},testcontroller));
        monk.setMemory(Colour.genBooleanMap());
        assertThrows(MoveNotAllowedException.class,()-> monk.activateCard(null,new String[]{"red","test"},testcontroller));
        monk.setMemory(Colour.genIntegerMap());
        assertThrows(MoveNotAllowedException.class,()-> monk.activateCard(null,new String[]{"red","test"},testcontroller));
        EnumMap<Colour,Integer> mem = monk.getMemory(Integer.class);
        mem.put(Colour.RED,1);
        assertThrows(MoveNotAllowedException.class,()-> monk.activateCard(null,new String[]{"red","test"},testcontroller));
        for(Island_Tile island_tile : testcontroller.getGame().getIslands()){
            System.out.println(island_tile.getTileID());
        }

        assertDoesNotThrow(()-> monk.activateCard(null,new String[]{"red",testcontroller.getGame().getIslands().get(0).getTileID()},testcontroller));
    }

    @Test
    void testActivateHerald(){
        CharID herald = CharID.HERALD;
        assertThrows(MoveNotAllowedException.class,()->herald.activateCard(null,new String[]{},testcontroller));
    }






}