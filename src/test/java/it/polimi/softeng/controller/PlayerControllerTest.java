package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.DiningRoomFullException;
import it.polimi.softeng.exceptions.InsufficientResourceException;
import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.SchoolBoard_Tile;
import it.polimi.softeng.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerControllerTest {
    private static final PlayerController playerController=new PlayerController();
    private static final boolean printTestResults=false;
    //generic function to generate n players with teams
    private static ArrayList<Player> generateTestTeam(int num) {
        ArrayList<String> playersNames=new ArrayList<>();
        for (int i=0; i<num; i++) {
            playersNames.add("Player_"+(i+1));
        }
        try {
            return playerController.genPlayers(playersNames);
        }
        catch (InvalidPlayerNumException ipne) {
            System.out.println("Could not gen players");
            return new ArrayList<>();
        }

    }
    @Test
    //Test for method genPlayers which generates players with teams
    public void testGenPlayers() {
        ArrayList<Player> testPlayers;
        for (int i=2; i<5; i++) {
            testPlayers=generateTestTeam(i);
            assertNotNull(testPlayers);
            assertEquals(i,testPlayers.size());
            ArrayList<Integer> teamSizes=new ArrayList<>();
            for (Team t: playerController.getTeams(testPlayers)) {
                teamSizes.add(playerController.getPlayersOnTeam(testPlayers,t).size());
            }
            for (int j=0; j<(teamSizes.size()-1); j++) {
                assertEquals(teamSizes.get(j),teamSizes.get(j+1));
            }
            if (printTestResults) {
                for (Player p: testPlayers) {
                    System.out.print(p.getName()+": Team="+p.getTeam());
                    if (p.getTeamMate()!=null) {
                        System.out.println(", Teammate="+p.getTeamMate().getName());
                    } else {
                        System.out.println(", Teammate=null");
                    }
                }
                System.out.println("Generating "+i+" players: Passed\n");
            }
        }
    }
    @Test
    //Test for method getTeams
    public void testGetTeam() {
        ArrayList<Player> testPlayers;
        ArrayList<Team> teams;
        for (int i=2; i<5; i++) {
            testPlayers=generateTestTeam(i);
            teams=playerController.getTeams(testPlayers);
            assertEquals(teams.size(),i==3?3:2);
            if (printTestResults) {
                System.out.println(testPlayers.size()+" players -> "+(i==3?3:2)+" teams ("+teams+"): Passed");
            }
        }
    }
    @Test
    //Test for method getPlayersOnTeam
    public void testGetPlayersOnTeam() {
        ArrayList<Player> testPlayers;
        ArrayList<Team> teams;
        ArrayList<Player> teamPlayers;
        int num;
        for (int i=2; i<5; i++) {
            testPlayers=generateTestTeam(i);
            teams=playerController.getTeams(testPlayers);
            if (printTestResults) {
                System.out.println();
                System.out.println(i+" players:");
            }
            num=0;
            for (Team t: teams) {
                teamPlayers=playerController.getPlayersOnTeam(testPlayers,t);
                num+=teamPlayers.size();
                if (printTestResults) {
                    System.out.println("Checking team "+t);
                }
                for (Player p: teamPlayers) {
                    assertEquals(p.getTeam(),t);
                    if (printTestResults) {
                        System.out.println(p.getName()+": "+p.getTeam());
                    }
                }
            }
            assertEquals(num,i);
        }
    }
    @Test
    public void testGetTeamColours() {
        Colour c=Colour.getRandomColour();
        ArrayList<Player> testPlayers;
        ArrayList<Colour> teamColours;

        for (int i=2; i<5; i++) {
            testPlayers = generateTestTeam(i);
            for (Player p: testPlayers) {
                p.setSchoolBoard(new SchoolBoard_Tile(null,0,0,0,null,0));
            }
            testPlayers.get(0).getSchoolBoard().setProfessor(c,true);
            teamColours=playerController.getTeamColours(testPlayers.get(0).getTeam(),testPlayers);
            assertEquals(c,teamColours.get(0));
            assertEquals(1,teamColours.size());
        }
    }

    @Test
    public void testInvalidPlayerNumExceptionOnPlayerGen() {
        ArrayList<String> playerNames=new ArrayList<>();
        assertThrows(InvalidPlayerNumException.class,()->playerController.genPlayers(playerNames));
        for (int i=0; i<5; i++) {
            playerNames.add("Player_"+i);
        }
        assertThrows(InvalidPlayerNumException.class,()->playerController.genPlayers(playerNames));
    }

    @Test
    public void testMoveStudentToDiningRoom() {
        Colour c=Colour.getRandomColour();
        ArrayList<Player> players=new ArrayList<>();
        Player testPlayer=new Player("test",null);
        testPlayer.setSchoolBoard(new SchoolBoard_Tile("test",10,0,0,null,0));
        players.add(testPlayer);
        Player professorPlayer=new Player("professor_test",null);
        professorPlayer.setSchoolBoard(new SchoolBoard_Tile(null,0,0,0,null,0));
        professorPlayer.getSchoolBoard().getContents().put(c,1);
        players.add(professorPlayer);
        assertDoesNotThrow(()->playerController.moveStudentToDiningRoom(professorPlayer,players,c,true));
        assertTrue(professorPlayer.getSchoolBoard().getProfessor(c));
        assertEquals(0,testPlayer.getSchoolBoard().getCoins());
        assertThrows(InsufficientResourceException.class,()->playerController.moveStudentToDiningRoom(testPlayer,players,c,true));
        testPlayer.getSchoolBoard().getContents().put(c,10);
        for (int i=0; i<10; i++) {
            assertDoesNotThrow(()->playerController.moveStudentToDiningRoom(testPlayer,players,c,true));
            assertEquals((i+1)/3,testPlayer.getSchoolBoard().getCoins());
        }
        testPlayer.getSchoolBoard().getContents().put(c,1);
        assertThrows(DiningRoomFullException.class,()->playerController.moveStudentToDiningRoom(testPlayer,players,c,true));
        assertTrue(testPlayer.getSchoolBoard().getProfessor(c));
        assertFalse(professorPlayer.getSchoolBoard().getProfessor(c));
    }

    @Test
    public void testSwapTowers() {
        int testSwapTowerNum=6;
        int testTowerNum=8;
        ArrayList<Player> testPlayers=generateTestTeam(2);
        for (Player p: testPlayers) {
            p.setSchoolBoard(new SchoolBoard_Tile(null,0,0,8,null,0));
        }
        //Simulates player 1 winning control over an island with 6 towers, they set 6 of their towers from their schoolboard on the island and the losing player receives their back
        testPlayers.get(0).getSchoolBoard().modifyTowers(testTowerNum);
        playerController.swapTeamTower(testPlayers,testPlayers.get(0).getTeam(),testPlayers.get(1).getTeam(),testSwapTowerNum);
        assertEquals(testTowerNum-testSwapTowerNum,testPlayers.get(0).getSchoolBoard().getTowers());
        assertEquals(testSwapTowerNum,testPlayers.get(1).getSchoolBoard().getTowers());
    }

    @Test
    public void testSwapFourPlayer() {
        int testSwapTowerNum=6;
        int testTowerNum=8;
        ArrayList<Player> testPlayers=generateTestTeam(4);
        for (Player p: testPlayers) {
            p.setSchoolBoard(new SchoolBoard_Tile(null,0,0,8,null,0));
        }
        //Simulates player 1 winning control over an island with 6 towers, they set 6 of their towers from their schoolboard on the island and the losing player receives their back
        ArrayList<Player> winningPlayers=playerController.getPlayersOnTeam(testPlayers,testPlayers.get(0).getTeam());
        ArrayList<Player> losingPlayers=playerController.getPlayersOnTeam(testPlayers,testPlayers.get(0).getTeam()==Team.WHITE?Team.BLACK:Team.WHITE);
        winningPlayers.get(0).getSchoolBoard().modifyTowers(5);
        winningPlayers.get(0).getSchoolBoard().modifyTowers(3);
        assertEquals(testTowerNum,winningPlayers.get(0).getSchoolBoard().getTowers()+winningPlayers.get(1).getSchoolBoard().getTowers());
        playerController.swapTeamTower(testPlayers,winningPlayers.get(0).getTeam(),losingPlayers.get(0).getTeam(),testSwapTowerNum);
        assertEquals(testTowerNum-testSwapTowerNum,winningPlayers.get(0).getSchoolBoard().getTowers()+winningPlayers.get(1).getSchoolBoard().getTowers());
        assertEquals(testSwapTowerNum,losingPlayers.get(0).getSchoolBoard().getTowers()+losingPlayers.get(1).getSchoolBoard().getTowers());
    }
}
