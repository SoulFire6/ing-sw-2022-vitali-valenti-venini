package it.polimi.softeng.controller;

import it.polimi.softeng.model.*;

import java.lang.reflect.Array;
import java.util.*;

public class Controller {
    private final Game game;
    TurnManager turnManager;
    int remainingMoves,maxMoves;

    public Controller(ArrayList<String> playerNames, boolean expertMode) {
        this.game=createGame(playerNames,expertMode);
        turnManager = new TurnManager(game.getPlayers());
        maxMoves = remainingMoves = game.getClouds().get(0).getMaxSlots();
    }
    public Game createGame(ArrayList<String> playerNames,boolean expertMode) {
        //TODO: complete method
        return null;
    }
    public boolean playAssistantCard(Player p, AssistantCard assistantCard)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }
        if(!p.getSchoolBoard().getHand().contains(assistantCard)) {
            System.out.println("Error. Card " + assistantCard.getCardID() + "can't be found in " + p.getName() + "'s hand");
            return false;
        }
        if(turnManager.getTurnState()!= TurnManager.TurnState.ASSISTANT_CARDS_PHASE)
        {
            System.out.println("Error. Operation not allowed");
            return false;
        }

        if(p.getSchoolBoard().getHand().size()>1)       //if the Player p hasn't just one card we check cards played by other players in this round
        {
            ArrayList<Player> previousPlayers = (ArrayList<Player>) turnManager.getPlayerOrder().subList(0,turnManager.getPlayerOrder().indexOf(p));
            for(Player player : previousPlayers)
            {
                if(player.getSchoolBoard().getLastUsedCard().getTurnValue() == assistantCard.getTurnValue())
                {
                    System.out.println("Error. Play another card");
                    return false;
                }
            }
        }

        p.getSchoolBoard().playAssistantCard(assistantCard.getCardID());
        turnManager.nextPlayer();
        return true;
    }

    public boolean playCharacterCard(CharacterCard characterCard, Player p)
    {
        if(!game.getCharacterCards().contains(characterCard))
        {
            System.out.println("Error. Character card not in play.");
            return false;
        }
        if(characterCard.getCost()>p.getSchoolBoard().getCoins())
        {
            System.out.println("Error. Not enough coins.");
            return false;
        }

        //Play Character card and increase its cost(1st activation)
        return true;
    }

    public boolean moveStudentsToIsland(Player p, EnumMap<Colour,Integer> students , Island_Tile island_tile)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }

        if(turnManager.getTurnState()!= TurnManager.TurnState.MOVE_STUDENTS_PHASE)
        {
            System.out.println("Error. Operation not allowed");
            return false;
        }

        for(Colour c : Colour.values()) {
            if(students.get(c) > turnManager.getCurrentPlayer().getSchoolBoard().getContents().get(c))
            {
                System.out.println("Error. Not enough students present in the entrance");
                return false;
            }
        }
        int movesRequested=0;
        for(Colour c : Colour.values())                 //count number of students that the player wants to insert
            movesRequested+=students.get(c);
        if(movesRequested>remainingMoves)
        {
            System.out.println("Error. You can move only up to" +remainingMoves +" students");
            return false;
        }
        island_tile.addStudents(students);
        remainingMoves-=movesRequested;
        if(remainingMoves==0) {
            turnManager.nextPlayer();
            remainingMoves=maxMoves;
        }
        return true;
    }

    public boolean moveMotherNature(Player p, int n)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }

        if(n>p.getSchoolBoard().getLastUsedCard().getMotherNatureValue())
        {
            System.out.println("Error. The given number is too high");
            return false;
        }

        if(turnManager.getTurnState()!= TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE)
        {
            System.out.println("Error. Operation not allowed");
            return false;
        }
        ArrayList<Island_Tile> islands = game.getIslands();
        Island_Tile oldMotherNatureIsland = null;
        Island_Tile newMotherNatureIsland;

        for(Island_Tile island : islands)                   //loop that assigns to oldMotherNatureIsland the actual MotherNature Island_Tile
            if(island.getMotherNature()) {
                oldMotherNatureIsland = island;
                break;
            }
        newMotherNatureIsland = oldMotherNatureIsland;

        for(int i=0;i<n;i++)                                //loop that positions newMotherNatureIsland to the wanted Island
            newMotherNatureIsland = newMotherNatureIsland.getNext();
        oldMotherNatureIsland.setMotherNature(false);       //MotherNature attribute on the Island_Tile gets changed
        newMotherNatureIsland.setMotherNature(true);
        checkInfluence(newMotherNatureIsland);
        checkUnification(newMotherNatureIsland);

        turnManager.nextPlayer();
        return true;
    }

    public boolean cloudDraw(Player p, Cloud_Tile cloud_tile)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }
        if(cloud_tile.isEmpty())
        {
            System.out.println("Error. Empty cloud tile.");
            return false;
        }
        if(turnManager.getTurnState()!= TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE)
        {
            System.out.println("Error. Operation not allowed");
            return false;
        }
        p.getSchoolBoard().fillEntrance(cloud_tile);
        turnManager.nextPlayer();
        return true;
    }

    public boolean moveStudentsToDiningRoom(Player p, EnumMap<Colour,Integer> students)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }

        if(turnManager.getTurnState()!= TurnManager.TurnState.MOVE_STUDENTS_PHASE)
        {
            System.out.println("Error. Operation not allowed");
            return false;
        }

        for(Colour c : Colour.values()) {
            if(students.get(c) > turnManager.getCurrentPlayer().getSchoolBoard().getContents().get(c))
            {
                System.out.println("Error. Not enough students present in the entrance");
                return false;
            }
            if(students.get(c) > (game.getDiningRoomMaxCapacity()-p.getSchoolBoard().getDiningRoomAmount(c)))
            {
                System.out.println("Error. Not enough space in the dining room");
                return false;
            }
        }

        int movesRequested=0;
        for(Colour c : Colour.values())                 //count number of students that the player wants to insert
            movesRequested+=students.get(c);
        if(movesRequested>remainingMoves)
        {
            System.out.println("Error. You can move only other" +remainingMoves +" students");
            return false;
        }

        for(Colour c : Colour.values())
            for(int i=0;i<students.get(c);i++) {
                turnManager.getCurrentPlayer().getSchoolBoard().moveStudentToDiningRoom(c);
                checkProfessorChange(c,p);
                remainingMoves--;
            }
        if(remainingMoves==0) {
            turnManager.nextPlayer();
            remainingMoves = maxMoves;
        }
        return true;
    }

    public void checkInfluence(Island_Tile motherNatureIsland)
    {
        int max=0, count=0;
        Team influenceTeam=null;
        EnumMap<Colour,Integer> content = motherNatureIsland.getContents();
        for(Team t : PlayerController.getTeams(game.getPlayers())) {
            for (Player p : game.getPlayers()) {
                if (p.getTeam() == t) {
                    for (Colour c : Colour.values()) {
                        if (p.getSchoolBoard().getProfessor(c))
                            count += content.get(c);
                    }
                    if (p.getTeam() == motherNatureIsland.getTeam() && p.getSchoolBoard().getMaxTowers()>0)         //if player's team controls the island and if the player is the one of the team with towers on the board
                        count += motherNatureIsland.getTowers();                                 //towers count as additional influence
                    if (count > max)
                        influenceTeam = t;
                    else if (count == max)
                        if (p.getTeam() == motherNatureIsland.getTeam())                        //If two teams got same influence, the previous influence lead is preserved
                            influenceTeam = t;
                    count = 0;
                }
            }
        }
        if(influenceTeam!=motherNatureIsland.getTeam()) {
            for(Player p:game.getPlayers()) {
                if (influenceTeam == p.getTeam() && p.getSchoolBoard().getMaxTowers() > 0)      //Add towers of influence team
                    p.getSchoolBoard().modifyTowers(motherNatureIsland.getTowers());
                if (motherNatureIsland.getTeam() == p.getTeam() && p.getSchoolBoard().getMaxTowers() >0)
                    p.getSchoolBoard().modifyTowers(motherNatureIsland.getTowers()*-1);     //Remove towers of the team who lost influence
            }
            motherNatureIsland.setTeam(influenceTeam);
        }


    }

    public void checkUnification(Island_Tile island)
    {
        ArrayList<Island_Tile> islands = game.getIslands();
        if(island.getTeam()==island.getNext().getTeam()) {
            island.setNext(island.getNext().getNext());
            island.setTowers(island.getTowers()+island.getNext().getTowers());
            islands.remove(island.getNext());
        }
        if(island.getTeam()==island.getPrev().getTeam()) {
            island.setPrev(island.getPrev().getPrev());
            island.setTowers(island.getTowers()+island.getPrev().getTowers());
            islands.remove(island.getPrev());
        }

    }

    public void checkProfessorChange(Colour c, Player p)
    {
        int count,max=0;
        Player professorPlayer=null;
        for(Player player : game.getPlayers()) {                           //set professorPlayer to the player controlling the professor of color c
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










}
