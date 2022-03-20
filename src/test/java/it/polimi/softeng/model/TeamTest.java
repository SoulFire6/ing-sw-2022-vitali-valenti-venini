package it.polimi.softeng.model;

import java.util.ArrayList;
import java.util.EnumMap;

import org.junit.Assert;
import org.junit.Test;

public class TeamTest {
    @Test
    public void testGenTeams() {
        ArrayList<Player> players=new ArrayList<>();
        EnumMap<Team,ArrayList<String>> teams;
        players.add(new Player("1"));
        players.add(new Player("2"));
        players.add(new Player("3"));
        //players.add(new Player("4"));
        teams=Team.genTeams(players,players.size());
        System.out.println(teams);
        Assert.assertNotNull(teams);
    }
}
