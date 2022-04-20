package it.polimi.softeng.controller;

import it.polimi.softeng.model.*;

import java.lang.reflect.Array;
import java.util.*;

public class Controller {
    Game g;
    TurnManager turnManager;

    public Controller(Game g)
    {
        this.g=g;
        turnManager = new TurnManager(g.getPlayers());
    }

    public boolean playAssistantCard(Player p, String assistantCardID)  //todo: check on assistant card values played by others
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }
        if(p.getSchoolBoard().getHand().stream().noneMatch(o -> o.getCardID().equals(assistantCardID))) {
            System.out.println("Error. Card " + assistantCardID + "can't be found in " + p.getName() + "'s hand");
            return false;
        }
        p.getSchoolBoard().playAssistantCard(assistantCardID);
        return true;
    }

    public boolean playCharacterCard()
    {
        return false;
    }

    public boolean moveStudentsToIsland(Player p, EnumMap<Colour,Integer> students , Island_Tile island_tile)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }

        for(Colour c : Colour.values()) {
            if(students.get(c) > turnManager.getCurrentPlayer().getSchoolBoard().getContents().get(c))
            {
                System.out.println("Error. Not enough students present in the entrance");
                return false;
            }
        }
        //students is contained in currentPlayer's diningRoom
        island_tile.addStudents(students);                      //todo: ricalcolo influenza e torri
        return true;
    }

    public boolean moveMotherNature(Player p, int n)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }

        if(n>p.getSchoolBoard().getLastUsedCard().getMotherNatureValue()) //todo: controllo character card +2
        {
            System.out.println("Error. The given number is too high");
            return false;
        }
        ArrayList<Island_Tile> islands = g.getIslands();
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
        p.getSchoolBoard().fillEntrance(cloud_tile);
        return true;
    }

    public boolean moveStudentsToDiningRoom(Player p, EnumMap<Colour,Integer> students)
    {
        if(p != turnManager.getCurrentPlayer()) {
            System.out.println("Can't execute this command, it's not the turn of player " + p.getName());
            return false;
        }

        for(Colour c : Colour.values()) {
            if(students.get(c) > turnManager.getCurrentPlayer().getSchoolBoard().getContents().get(c))
            {
                System.out.println("Error. Not enough students present in the entrance");
                return false;
            }
        }

        for(Colour c : Colour.values())
            for(int i=0;i<students.get(c);i++)
                turnManager.getCurrentPlayer().getSchoolBoard().moveStudentToDiningRoom(c);

        return true;
    }













}
