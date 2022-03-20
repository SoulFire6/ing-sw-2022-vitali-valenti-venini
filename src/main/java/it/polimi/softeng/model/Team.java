package it.polimi.softeng.model;

import java.util.EnumMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public enum Team {
    WHITE,BLACK,GREY;

    public static EnumMap<Team,ArrayList<String>> genTeams(ArrayList<Player> players, Integer playerNum) {
        Random rand=new Random();
        Integer idx;
        String temp;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<playerNum; i++) {
            playerNames.add(players.get(i).getName());
        }
        for (int i=0; i<playerNum; i++) {
            idx=rand.nextInt(playerNum);
            temp=playerNames.get(idx);
            playerNames.set(idx,playerNames.get(i));
            playerNames.set(i,temp);
        }
        if (playerNum==3) {
            return threeTeamGame(playerNames);
        } else {
            return twoTeamGame(playerNames);
        }
    }

    public static EnumMap<Team,ArrayList<String>> threeTeamGame(ArrayList<String> playerNames) {
        Integer i=0;
        EnumMap<Team,ArrayList<String>> teams=new EnumMap<>(Team.class);
        for (Team t:Team.values()) {
            teams.put(t,new ArrayList<>(Arrays.asList(playerNames.get(i))));
            i++;
        }
        return teams;
    }
    public static EnumMap<Team,ArrayList<String>> twoTeamGame(ArrayList<String> playerNames) {
        EnumMap<Team,ArrayList<String>> teams=new EnumMap<>(Team.class);
        ArrayList<String> whiteTeam = new ArrayList<>(Arrays.asList(playerNames.get(0)));
        ArrayList<String> blackTeam = new ArrayList<>(Arrays.asList(playerNames.get(0)));
        if (playerNames.size()==4) {
            whiteTeam.add(playerNames.get(2));
            blackTeam.add(playerNames.get(3));
        }
        teams.put(Team.WHITE,whiteTeam);
        teams.put(Team.BLACK,blackTeam);
        return teams;
    }
}
