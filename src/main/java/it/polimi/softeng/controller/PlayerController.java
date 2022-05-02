package it.polimi.softeng.controller;

import it.polimi.softeng.controller.Exceptions.*;
import it.polimi.softeng.model.*;

import java.util.*;

public class PlayerController {

    public ArrayList<Player> genPlayers(ArrayList<String> playerNames) {
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
    public ArrayList<Team> getTeams(ArrayList<Player> players) {
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

    public void cloudDraw(Player p, Cloud_Tile cloud_tile, TurnManager turnManager) throws NotYourTurnException, EmptyCloudTileException, MoveNotAllowedException {
        if(p != turnManager.getCurrentPlayer())
            throw new NotYourTurnException("Can't execute this command, it's not the turn of player " + p.getName());

        if(cloud_tile.isEmpty())
            throw new EmptyCloudTileException("Error. Empty cloud tile.");

        if(turnManager.getTurnState()!= TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE)
            throw new MoveNotAllowedException("Error. Operation not allowed");

        p.getSchoolBoard().fillEntrance(cloud_tile);
        turnManager.nextAction();
    }

    public void moveStudentsToDiningRoom(Player p, EnumMap<Colour,Integer> students, TurnManager turnManager, Game game, int maxMoves) throws NotYourTurnException, MoveNotAllowedException, NotEnoughStudentsInEntranceException, NotEnoughSpaceInDiningRoomException {
        if(p != turnManager.getCurrentPlayer())
            throw new NotYourTurnException("Can't execute this command, it's not the turn of player " + p.getName());

        if(turnManager.getTurnState()!= TurnManager.TurnState.MOVE_STUDENTS_PHASE)
            throw new MoveNotAllowedException("Error. Operation not allowed");

        for(Colour c : Colour.values()) {
            if(students.get(c) > turnManager.getCurrentPlayer().getSchoolBoard().getContents().get(c))
                throw new NotEnoughStudentsInEntranceException("Error. Not enough students in the entrance");

            if(students.get(c) > (10-p.getSchoolBoard().getDiningRoomAmount(c)))
                throw new NotEnoughSpaceInDiningRoomException("Error. Not enough space in the dining room");
        }

        int entranceDiscs=0;                                //num of discs in the entrance
        for(Colour c: Colour.values())
            entranceDiscs+=p.getSchoolBoard().getContents().get(c);
        int remainingMoves = maxMoves - p.getSchoolBoard().getMaxExntranceSlots() + entranceDiscs;

        int movesRequested=0;
        for(Colour c : Colour.values())                 //count number of students that the player wants to insert
            movesRequested+=students.get(c);
        if(movesRequested>remainingMoves)
            throw new NotEnoughStudentsInEntranceException("Error. You can move only up to" +remainingMoves +" students");

        for(Colour c : Colour.values())
            for(int i=0;i<students.get(c);i++) {
                p.getSchoolBoard().moveStudentToDiningRoom(c);
                checkProfessorChange(c,p,game.getPlayers());
                remainingMoves--;
            }
        if(remainingMoves==0)                           //if the exact number of moves has been completed by the player
            turnManager.nextAction();

    }

    public void checkProfessorChange(Colour c, Player p,ArrayList<Player> players)
    {
        int count,max=0;
        Player professorPlayer=null;
        for(Player player : players) {                           //set professorPlayer to the player controlling the professor of color c
            if(player.getSchoolBoard().getProfessor(c))
                professorPlayer = player;
        }
        if(professorPlayer==null)                                       //First dining room filled
        {
            p.getSchoolBoard().setProfessor(c,true);
            return;
        }
        if(p.getSchoolBoard().getDiningRoomAmount(c)>professorPlayer.getSchoolBoard().getDiningRoomAmount(c)) {
            p.getSchoolBoard().setProfessor(c, true);
            professorPlayer.getSchoolBoard().setProfessor(c,false);
        }
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
