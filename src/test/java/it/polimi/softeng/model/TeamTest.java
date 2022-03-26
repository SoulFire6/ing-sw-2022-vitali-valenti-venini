package it.polimi.softeng.model;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TeamTest {
    private static boolean printTestResults=false;
    //generic function to generate n players with teams
    public ArrayList<Player> generateTestTeam(Integer num) {
        ArrayList<String> playersNames=new ArrayList<>();
        for (int i=0; i<num; i++) {
            playersNames.add("Player_"+(i+1));
        }
        return Team.genTeams(playersNames);
    }
    @Test
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
}
