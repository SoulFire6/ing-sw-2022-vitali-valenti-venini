package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.DiningRoomFullException;
import it.polimi.softeng.exceptions.InsufficientResourceException;

import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Collections;


public class PlayerController {

    public ArrayList<Player> genPlayers(ArrayList<String> playerNames) throws InvalidPlayerNumException{
        Random rand=new Random();
        ArrayList<Player> players=new ArrayList<>();
        int playerNum=playerNames.size();
        if (playerNum<2 || playerNum>4) {
            throw new InvalidPlayerNumException("Could not generate teams\nExpected players: 2-4\nActual: "+playerNum);
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
    public ArrayList<Team> getTeams(ArrayList<Player> players) {
        ArrayList<Team> teams= new ArrayList<>();
        for (Player p: players) {
            if (!teams.contains(p.getTeam())) {
                teams.add(p.getTeam());
            }
        }
        return teams;
    }

    public ArrayList<Player> getPlayersOnTeam(ArrayList<Player> players, Team t) {
        ArrayList<Player> res=new ArrayList<>();
        for (Player p: players) {
            if (p.getTeam()==t) {
                res.add(p);
            }
        }
        return res;
    }
    public ArrayList<Colour> getTeamColours(Team t, ArrayList<Player> players) {
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
    public void moveStudentToDiningRoom(Player p, ArrayList<Player> players, Colour c, boolean expertMode, TurnManager turnManager) throws InsufficientResourceException,DiningRoomFullException {
        if (p.getSchoolBoard().getContents().get(c)==0) {
            throw new InsufficientResourceException("Not enough "+c+" students in entrance");
        }
        if (p.getSchoolBoard().getDiningRoomAmount(c)==10) {
            throw new DiningRoomFullException(c+" dining room is full");
        }
        p.getSchoolBoard().moveStudentToDiningRoom(c);
        if (expertMode && p.getSchoolBoard().getDiningRoomAmount(c)%3==0) {
            p.getSchoolBoard().setCoins(p.getSchoolBoard().getCoins()+1);
        }
        Player professorPlayer=null;
        for (Player player : players) {
            if (player.getSchoolBoard().getProfessor(c)) {
                professorPlayer=player;
            }
        }
        if (professorPlayer==null) {
            p.getSchoolBoard().setProfessor(c,true);
        } else {
            if (p!=professorPlayer && p.getSchoolBoard().getDiningRoomAmount(c)>professorPlayer.getSchoolBoard().getDiningRoomAmount(c)) {
                professorPlayer.getSchoolBoard().setProfessor(c,false);
                p.getSchoolBoard().setProfessor(c,true);
            }
        }
        turnManager.nextAction();
    }

    public void swapTeamTower(ArrayList<Player> players, Team winningTeam, Team losingTeam, int towerNum) {
        if (players.size()==4) {
            ArrayList<Player> winningPlayers=getPlayersOnTeam(players,winningTeam);
            ArrayList<Player> losingPlayers=getPlayersOnTeam(players,losingTeam);
            for (int i=0; i<towerNum; i++) {
                if (winningPlayers.get(0).getSchoolBoard().getTowers()>winningPlayers.get(1).getSchoolBoard().getTowers()) {
                    winningPlayers.get(0).getSchoolBoard().modifyTowers(-1);
                } else {
                    winningPlayers.get(1).getSchoolBoard().modifyTowers(-1);
                }
                if (losingPlayers.get(0).getSchoolBoard().getTowers()>losingPlayers.get(1).getSchoolBoard().getTowers()) {
                    losingPlayers.get(1).getSchoolBoard().modifyTowers(1);
                } else {
                    losingPlayers.get(0).getSchoolBoard().modifyTowers(1);
                }
            }
        } else {
            for (Player p: players) {
                if (p.getTeam()==winningTeam) {
                    p.getSchoolBoard().modifyTowers(-towerNum);
                }
                if (p.getTeam()==losingTeam) {
                    p.getSchoolBoard().modifyTowers(towerNum);
                }
            }
        }
    }
}
