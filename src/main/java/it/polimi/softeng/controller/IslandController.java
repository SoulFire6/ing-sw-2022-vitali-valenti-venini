package it.polimi.softeng.controller;

import it.polimi.softeng.model.*;

import java.util.*;

public class IslandController {
    public static ArrayList<Island_Tile> genIslands(int num, Bag_Tile bag) {
        ArrayList<Island_Tile> islands=new ArrayList<>();
        ArrayList<String> initialisedIslands=new ArrayList<>();
        Island_Tile head=new Island_Tile("Island_1");
        Island_Tile curr=head;
        islands.add(head);
        for (int i=1; i<num; i++) {
            curr.setNext(new Island_Tile("Island_"+(i+1)));
            islands.add(curr.getNext());
            curr.getNext().setPrev(curr);
            curr=curr.getNext();
        }
        curr.setNext(head);
        head.setPrev(curr);
        Random rand=new Random();
        int randIsland=Math.max(1,rand.nextInt(num));
        int opposite=((num/2)+randIsland);
        if (opposite>num) {
            opposite%=num;
        }
        boolean foundMNISLE=false;
        boolean foundOpposite=false;
        for (Island_Tile island: islands) {
            if (island.getTileID().equals("Island_"+randIsland)) {
                island.setMotherNature(true);
                initialisedIslands.add(island.getTileID());
                foundMNISLE=true;
            }
            if (island.getTileID().equals("Island_"+opposite)) {
                initialisedIslands.add(island.getTileID());
                foundOpposite=true;
            }
            if (foundMNISLE && foundOpposite) {
                break;
            }
        }
        //Puts a student on every island except the one with mother nature and the one opposite
        for (Island_Tile island: islands) {
            if (!initialisedIslands.contains(island.getTileID())) {
                island.setContents(bag.drawStudents(1));
                initialisedIslands.add(island.getTileID());
            }
        }
        return islands;
    }
    //NORMAL: charController and cards are null, otherwise calculates with EXPERT mode rules
    public Team calculateInfluence(Player conqueringPlayer, Island_Tile island, ArrayList<Player> players, CharCardController charController,  ArrayList<CharacterCard> cards) {
        Team conqueringTeam=conqueringPlayer.getTeam();
        Team currentTeam=island.getTeam();
        EnumMap<Team,Integer> teamInfluence=new EnumMap<>(Team.class);
        EnumMap<Team,ArrayList<Colour>> teamColours=new EnumMap<>(Team.class);
        //Setup
        for (Team t: Team.values()) {
            teamInfluence.put(t,0);
            teamColours.put(t,PlayerController.getTeamColours(t,players));
        }
        //Tower influence
        if (charController==null) {
            teamInfluence.put(currentTeam,teamInfluence.get(currentTeam)+island.getTowers());
        } else {
            //centaur negates tower influence of current island, knight adds 2 to initial influence to conquering player's team
            teamInfluence.put(currentTeam,teamInfluence.get(currentTeam)+(island.getTowers()*(charController.getActiveStatus("CENTAUR")?0:1)));
            teamInfluence.put(conqueringTeam,teamInfluence.get(conqueringTeam)+2*(charController.getActiveStatus("KNIGHT")?1:0));
        }
        //Student Disk Influence
        for (Colour c: Colour.values()) {
            if (charController==null || !charController.checkDisabledColour(c, cards)) {
                for (Team t: Team.values()) {
                    if (teamColours.get(t).contains(c)) {
                        teamInfluence.put(t,teamInfluence.get(t)+island.getContents().get(c));
                    }
                }
            }
        }
        Team maxTeam= Collections.max(teamInfluence.entrySet(), Map.Entry.comparingByValue()).getKey();
        int maxInfluence=teamInfluence.get(maxTeam);
        //If two or more teams have the same max influence previous island state is returned
        for (Team t: Team.values()) {
            if (t!=maxTeam && teamInfluence.get(t)==maxInfluence) {
                return island.getTeam();
            }
        }
        island.setTeam(maxTeam);
        //TODO: swap out towers (evenly among team members?)
        return maxTeam;
    }
    public static ArrayList<Island_Tile> checkAndMerge(ArrayList<Island_Tile> islands, Island_Tile island) {
        if (islands.contains(island) && islands.size()>3) {
            if (island.getTeam()==island.getNext().getTeam()) {
                island.setTowers(island.getTowers()+island.getNext().getTowers());
                island.addStudents(island.getNext().getContents());
                islands.remove(island.getNext());
                island.setNext(island.getNext().getNext());
            }
            if (island.getTeam()==island.getPrev().getTeam()) {
                island.setTowers(island.getTowers()+island.getPrev().getTowers());
                island.addStudents(island.getPrev().getContents());
                islands.remove(island.getPrev());
                island.setPrev(island.getPrev().getPrev());
            }
        }
        return islands;
    }
}
