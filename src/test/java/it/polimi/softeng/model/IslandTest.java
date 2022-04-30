package it.polimi.softeng.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class IslandTest {
    @Test
    public void testIslandConstructor() {
        String testID="test";
        Island_Tile island=new Island_Tile(testID);
        assertEquals(testID,island.getTileID());
        assertFalse(island.getMotherNature());
        assertEquals(0,island.getTowers());
        assertNull(island.getTeam());
        assertNull(island.getNext());
        assertNull(island.getPrev());
        assertFalse(island.getNoEntry());
    }
    @Test
    public void testMotherNature() {
        Island_Tile island=new Island_Tile("MotherNatureTest");
        island.setMotherNature(true);
        assertEquals(true,island.getMotherNature());
    }
    @Test
    public void testTower() {
        int num=8;
        Island_Tile island=new Island_Tile("TowerTest");
        island.setTowers(num);
        assertEquals(num,island.getTowers());
    }
    @Test
    public void testTeam() {
        Island_Tile island=new Island_Tile("TeamTest");
        assertNull(island.getTeam());
        for (Team team: Team.values()) {
            island.setTeam(team);
            assertEquals(team,island.getTeam());
        }
    }
    @Test
    public void testPointers() {
        String islandOneID="1";
        String islandTwoID="2";
        Island_Tile one=new Island_Tile(islandOneID);
        Island_Tile two=new Island_Tile(islandTwoID);
        one.setNext(two);
        assertEquals(two,one.getNext());
        one.getNext().setPrev(one);
        assertNotEquals(one,one.getNext());
        assertEquals(one,one.getNext().getPrev());
    }
    @Test
    public void testNoEntry() {
        Island_Tile island=new Island_Tile("NoEntryTest");
        island.setNoEntry(true);
        assertTrue(island.getNoEntry());
    }
    /*
    @Test
    public void testGenIslands() {
        int num=12;
        int sum=0;
        int motherNatureNum=0;
        ArrayList<Island_Tile> islands=IslandController.genIslands(num, new Bag_Tile(num-2));
        assertEquals(num,islands.size());
        for (Island_Tile island: islands) {
            if (island.getMotherNature()) {
                assertEquals(0,island.getFillAmount());
                motherNatureNum++;
            }
            sum+=island.getFillAmount();
        }
        assertEquals(1,motherNatureNum);
        assertEquals(num-2,sum);
    }
    TODO: move to controller test
     */
}
