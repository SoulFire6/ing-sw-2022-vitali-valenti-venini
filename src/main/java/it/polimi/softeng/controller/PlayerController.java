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


/**
 Controller class that handles players generation and player utilities function
 */
public class PlayerController {
    /**
     * This method is used to get all the teams of the current game
     * @param players players of the current game
     * @return ArrayList of Team, all of the teams that play the game
     */
    public ArrayList<Team> getTeams(ArrayList<Player> players) {
        ArrayList<Team> teams= new ArrayList<>();
        for (Player p: players) {
            if (!teams.contains(p.getTeam())) {
                teams.add(p.getTeam());
            }
        }
        return teams;
    }


    /**
     * This method generates players with given names
     * @param playerNames the names of the players of the game
     * @exception InvalidPlayerNumException when player number is greater than 4 or smaller than 2
     * @return ArrayList of Player, the generated players
     */
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

    /**
     * This method is used to know which players are from a certain team
     * @param players all the players of the current game
     * @param t the team from which we want to know the players
     * @return ArrayList of Player, the players from the Team t
     */
    public ArrayList<Player> getPlayersOnTeam(ArrayList<Player> players, Team t) {
        ArrayList<Player> res=new ArrayList<>();
        for (Player p: players) {
            if (p.getTeam()==t) {
                res.add(p);
            }
        }
        return res;
    }

    /**
     * This method is used to calculate the combined disks present owned by a team in its players dining rooms
     * @param t the team we want to calculate the disks from
     * @param players all the players of the current game
     * @return ArrayList of Colour, all the disks owned by the team
     */
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

    /**
     * This method is used from a player to move the students from the entrance to his dining room
     * @param  p the player who wants to move the students
     * @param players all the players of the current game
     * @param c the Colour of the disk the player wants to move to his dining room
     * @param expertMode true if the game is in expertMode, false otherwise
     * @exception InsufficientResourceException when player p doesn't have a student of the specified colour to move from their entrance
     * @exception DiningRoomFullException when player p is trying to fill a row of the dining room that is already full
     */

    public void moveStudentToDiningRoom(Player p, ArrayList<Player> players, Colour c, boolean expertMode) throws InsufficientResourceException,DiningRoomFullException {
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
            if (p.getSchoolBoard().getDiningRoomAmount(c)>professorPlayer.getSchoolBoard().getDiningRoomAmount(c)) {
                professorPlayer.getSchoolBoard().setProfessor(c,false);
                p.getSchoolBoard().setProfessor(c,true);
            }
        }
    }

    /**
     * This method is used to increment/decrement the number of towers owned by a player or by a Team (case of 4-players game)
     * @param players all the players of the current game
     * @param winningTeam the team that won towers
     * @param losingTeam the team that lost towers
     * @param towerNum the number of towers that got won/lost
     */
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
