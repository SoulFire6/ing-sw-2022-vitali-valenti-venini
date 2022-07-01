package it.polimi.softeng.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test class for Island
 */
public class IslandTest {

    /**
     * Tests default constructor
     */
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

    /**
     * Tests setter and getters for mother nature
     */
    @Test
    public void testMotherNature() {
        Island_Tile island=new Island_Tile("MotherNatureTest");
        island.setMotherNature(true);
        assertEquals(true,island.getMotherNature());
    }

    /**
     * Tests setter and getters for towers
     */
    @Test
    public void testTower() {
        int num=8;
        Island_Tile island=new Island_Tile("TowerTest");
        island.setTowers(num);
        assertEquals(num,island.getTowers());
    }
    /**
     * Tests setter and getters for team
     */
    @Test
    public void testTeam() {
        Island_Tile island=new Island_Tile("TeamTest");
        assertNull(island.getTeam());
        for (Team team: Team.values()) {
            island.setTeam(team);
            assertEquals(team,island.getTeam());
        }
    }
    /**
     * Tests pointers next and prev
     */
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

    /**
     * Tests setter and getters no entry tile
     */
    @Test
    public void testNoEntry() {
        Island_Tile island=new Island_Tile("NoEntryTest");
        island.setNoEntry(true);
        assertTrue(island.getNoEntry());
    }
}
