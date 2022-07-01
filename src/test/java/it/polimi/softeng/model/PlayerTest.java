package it.polimi.softeng.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for Player
 */
public class PlayerTest {
    /**
     * Tests Player constructor
     */
    @Test
    public void testPlayerConstructor() {
        String testName="test_player";
        Team testTeam=Team.WHITE;
        Player testPlayer=new Player(testName,testTeam);
        assertEquals(testName,testPlayer.getName());
        assertEquals(testTeam,testPlayer.getTeam());
        assertNull(testPlayer.getTeamMate());
        assertNull(testPlayer.getSchoolBoard());
    }
    /**
     * Tests setters and getters for teamMate
     */
    @Test
    public void testTeamMate() {
        Player playerOne=new Player("one",Team.WHITE);
        Player playerTwo=new Player("two",Team.WHITE);
        playerOne.setTeamMate(playerTwo);
        playerTwo.setTeamMate(playerOne);
        assertEquals(playerOne,playerTwo.getTeamMate());
        assertEquals(playerTwo,playerOne.getTeamMate());
        assertEquals(playerOne.getTeam(),playerTwo.getTeam());
    }
    /**
     * Tests setters and getters for schoolBoard
     */
    @Test
    public void testSchoolBoard() {
        Player testPlayer=new Player("test",Team.WHITE);
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile(testPlayer.getName(),0,0,0,null,0);
        testPlayer.setSchoolBoard(schoolBoard);
        assertEquals(schoolBoard,testPlayer.getSchoolBoard());
    }
}
