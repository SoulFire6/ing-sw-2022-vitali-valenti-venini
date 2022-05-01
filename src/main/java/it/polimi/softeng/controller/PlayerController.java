package it.polimi.softeng.controller;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class PlayerController {
    public static ArrayList<Player> genTeams(ArrayList<String> playerNames) {
        Random rand=new Random();
        ArrayList<Player> players=new ArrayList<>();
        int playerNum=playerNames.size();
        if (playerNum<2 || playerNum>4) {
            throw new IllegalArgumentException("Could not generate teams\nExpected players: 2-4\nActual: "+playerNum);
        }
        //Randomises what teams each player gets by shuffling their names
        for (int i=0; i<playerNum; i++) {
            Collections.swap(playerNames,i,rand.nextInt(playerNum));
        }
        ArrayList<Team> teams=new ArrayList<>(Arrays.asList(Team.WHITE,Team.BLACK));
        if (playerNum==3) {
            teams.add(Team.GREY);
            for (int i=0; i<playerNum; i++) {
                players.add(new Player(playerNames.get(i),teams.get(i)));
            }
        } else {
            for (int i=0; i<playerNum; i++) {
                players.add(new Player(playerNames.get(i),teams.get(i%2)));
            }
            if (playerNum==4) {
                for (int i=0; i<2; i++) {
                    players.get(i).setTeamMate(players.get(i+2));
                    players.get(i+2).setTeamMate(players.get(i));
                }
            }
        }
        //Randomises player order
        for (int i=0; i<playerNum; i++) {
            Collections.swap(players,i,rand.nextInt(playerNum));
        }
        return players;
    }
    public static ArrayList<Team> getTeams(ArrayList<Player> players) {
        ArrayList<Team> teams= new ArrayList<>();
        for (Player p: players) {
            if (!teams.contains(p.getTeam())) {
                teams.add(p.getTeam());
            }
        }
        return teams;
    }
    public static ArrayList<Player> getPlayersOnTeam(ArrayList<Player> players, Team t) {
        ArrayList<Player> res=new ArrayList<>();
        for (Player p: players) {
            if (p.getTeam()==t) {
                res.add(p);
            }
        }
        return res;
    }
    public static ArrayList<Colour> getTeamColours(Team t, ArrayList<Player> players) {
        ArrayList<Colour> colours=new ArrayList<>();
        for (Player p: players) {
            if (p.getTeam()==t) {
                for (Colour c: Colour.values()) {
                    if (p.getSchoolBoard().getProfessor(c) && !colours.contains(c)) {
                        colours.add(c);
                    }
                }
            }
        }
        return colours;
    }
    public static void swapTeamTower(ArrayList<Player> players, Team winningTeam, Team losingTeam, int towerNum) {
        if (players.size()==4) {
            ArrayList<Player> winningPlayers=getPlayersOnTeam(players,winningTeam);
            ArrayList<Player> losingPlayers=getPlayersOnTeam(players,losingTeam);
            for (int i=0; i<towerNum; i++) {
                winningPlayers.get(i%2).getSchoolBoard().modifyTowers(1);
                losingPlayers.get(i%2).getSchoolBoard().modifyTowers(-1);
            }
        } else {
            for (Player p: players) {
                if (p.getTeam()==winningTeam) {
                    p.getSchoolBoard().modifyTowers(towerNum);
                }
                if (p.getTeam()==losingTeam) {
                    p.getSchoolBoard().modifyTowers(-towerNum);
                }
            }
        }

    }
}
