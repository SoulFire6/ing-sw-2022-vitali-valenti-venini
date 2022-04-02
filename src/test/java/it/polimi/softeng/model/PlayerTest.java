package it.polimi.softeng.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class PlayerTest {
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
    @Test
    public void testSchoolBoard() {
        Player testPlayer=new Player("test",Team.WHITE);
        SchoolBoard_Tile schoolBoard=new SchoolBoard_Tile(testPlayer.getName(),0,0,0);
        testPlayer.setSchoolBoard(schoolBoard);
        assertEquals(schoolBoard,testPlayer.getSchoolBoard());
    }
}
