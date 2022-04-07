package it.polimi.softeng.model;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TeamTest {
    private static boolean printTestResults=false;
    //generic function to generate n players with teams
    private static ArrayList<Player> generateTestTeam(int num) {
        ArrayList<String> playersNames=new ArrayList<>();
        for (int i=0; i<num; i++) {
            playersNames.add("Player_"+(i+1));
        }
        return Team.genTeams(playersNames);
    }
    @Test
    //Test for method genTeams
    public void testGenTeam() {
        ArrayList<Player> testPlayers;
        for (int i=2; i<5; i++) {
            testPlayers=generateTestTeam(i);
            Assert.assertNotNull("Generating "+i+" players: Failed",testPlayers);
            Assert.assertEquals("Improper amount of players",i,testPlayers.size());
            ArrayList<Integer> teamSizes=new ArrayList<>();
            for (Team t: Team.getTeams(testPlayers)) {
                teamSizes.add(Team.getPlayersOnTeam(testPlayers,t).size());
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
            teams=Team.getTeams(testPlayers);
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
            teams=Team.getTeams(testPlayers);
            if (printTestResults) {
                System.out.println();
                System.out.println(i+" players:");
            }
            num=0;
            for (Team t: teams) {
                teamPlayers=Team.getPlayersOnTeam(testPlayers,t);
                num+=teamPlayers.size();
                if (printTestResults) {
                    System.out.println("Checking team "+t);
                }
                for (Player p: teamPlayers) {
                    assertEquals("Player initialised with the wrong team",p.getTeam(),t);
                    if (printTestResults) {
                        System.out.println(p.getName()+": "+p.getTeam());
                    }
                }
            }
            assertEquals("Player number mismatch",num,i);
        }
    }
}
