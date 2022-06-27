package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;


import java.util.*;


/**
 Controller class that handles islands, clouds and the movement of mother nature

 */

public class TileController {

    /**
     * This method is used to generate and initialize the islands of the current game
     * @param num the number of islands to be generated
     * @param bag the Bag_Tile object of the current game
     * @return ArrayList<Island_Tile> the list of generated islands
     */
    public ArrayList<Island_Tile> genIslands(int num, Bag_Tile bag) {
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

    /**
     * This method is used to generate and initialize the cloud tiles of the current game
     * @param num the number of cloud tiles to be generated
     * @param max the maximum number of student disks that can be held on a cloud tile
     * @param bag the Bag_Tile object of the current game
     * @return ArrayList<Cloud_Tile> the generated cloud tiles
     */
    public ArrayList<Cloud_Tile> genClouds(int num, int max, Bag_Tile bag) {
        ArrayList<Cloud_Tile> clouds=new ArrayList<>();
        for (int i=0; i<num; i++) {
            clouds.add(new Cloud_Tile("Cloud_"+(i+1),max));
        }
        for (Cloud_Tile cloud: clouds) {
            cloud.fillCloud(bag);
        }
        return clouds;
    }

    /**
     * This method is used to move mother nature across the islands
     * @param p the player who want to perform the operation
     * @param n the number of movement that we want mother nature to perfom
     * @param charCardController the charCardController of the current game
     * @param players list of all the players of the current game
     * @param cards list of the usable character cards of this game
     * @param islands list of the islands of this game
     * @param playerController the playerController of this game
     * @exception ExceededMaxMovesException when the player wants to move mother nature more than possible
     * @exception GameIsOverException when
     * @return boolean true if the operation succeeded, false otherwise
     */

    public boolean moveMotherNature(Player p, int n, CharCardController charCardController, ArrayList<Player> players, ArrayList<CharacterCard> cards, ArrayList<Island_Tile> islands,PlayerController playerController) throws ExceededMaxMovesException,GameIsOverException {
        int maxAmount=p.getSchoolBoard().getLastUsedCard().getMotherNatureValue();
        if (charCardController!=null && charCardController.getActiveStatus(CharID.MAGIC_POSTMAN,cards)) {
            maxAmount+=2;
        }
        if (n>maxAmount) {
            throw new ExceededMaxMovesException("Cannot move mother nature by "+n+" (Current max: "+maxAmount+")");
        }
        //loop that assigns to oldMotherNatureIsland the actual MotherNature Island_Tile
        for (Island_Tile island: islands) {
            if (island.getMotherNature()) {
                island.setMotherNature(false);
                for (int i=0; i<n; i++) {
                    island=island.getNext();
                }
                island.setMotherNature(true);
                if (!island.getNoEntry()) {
                    Team currentTeam=island.getTeam();
                    calculateInfluence(p,island,players,charCardController,cards,playerController);
                    if (currentTeam!=island.getTeam()) {
                        checkAndMerge(islands,island);
                    }
                } else {
                    for (CharacterCard card : cards) {
                        if (card.getCharacter().equals(CharID.GRANDMA_HERBS)) {
                            card.getCharacter().setMemory(card.getCharacter().getMemory()+1);
                        }
                    }
                }
                break;
            }
        }
        return false;
    }

    /**
     * This method is used to move students from the entrance of a schoolboard to an Island
     * @param p the player who want to perform the operation
     * @param c the colour of the student disk the player wants to move
     * @param islandID the ID of the island where the player wants to move the student disk
     * @param islands list of the islands of this game
     * @exception TileNotFoundException when the islandID isn't found
     * @exception InsufficientResourceException when there isn't any student of the wanted colour in the player's entrance
     */
    public void moveStudentsToIsland(Player p, Colour c, String islandID, ArrayList<Island_Tile> islands) throws TileNotFoundException, InsufficientResourceException {
        Island_Tile chosenIsland=null;
        for (Island_Tile island: islands) {
            if (island.getTileID().equalsIgnoreCase(islandID) || island.getTileID().equalsIgnoreCase("Island_"+islandID)) {
                chosenIsland=island;
            }
        }
        if (chosenIsland==null) {
            throw new TileNotFoundException("Could not find island with id "+islandID);
        }
        if (p.getSchoolBoard().getContents().get(c)==0) {
            throw new InsufficientResourceException("Entrance contains no "+c+" student disks");
        }
        chosenIsland.addColour(c,1);
        p.getSchoolBoard().getContents().put(c,p.getSchoolBoard().getContents().get(c)-1);
    }
    /**
     * This method is used to fill the clouds from the bag
     * @param clouds the list of all the clouds of the current game
     * @param bag the Bag_Tile object of the current game
     * @exception TileNotEmptyException when invoked while there is at least one non-empty cloud
     */
    public void refillClouds(ArrayList<Cloud_Tile> clouds, Bag_Tile bag) throws TileNotEmptyException {
        for (Cloud_Tile cloud : clouds) {
            if (cloud.getFillAmount()>0) {
                throw new TileNotEmptyException(cloud.getTileID()+" was not empty before refill");
            }
            cloud.fillCloud(bag);
        }
    }
    public void refillEntranceFromCloud(Player p, String cloudID, ArrayList<Cloud_Tile> clouds) throws TileNotFoundException,TileEmptyException,MoveNotAllowedException {
        Cloud_Tile refillCloud=null;
        for (Cloud_Tile cloud: clouds) {
            if (cloud.getTileID().equalsIgnoreCase(cloudID) || cloud.getTileID().equalsIgnoreCase("Cloud_"+cloudID)) {
                refillCloud=cloud;
            }
        }
        if (refillCloud==null) {
            throw new TileNotFoundException("Cloud with id "+cloudID+" could not be found");
        }
        if (refillCloud.isEmpty()) {
            throw new TileEmptyException("Cannot refill from empty cloud");
        }
        p.getSchoolBoard().fillEntrance(refillCloud);
    }
    //NORMAL: charController and cards are null, otherwise calculates with EXPERT mode rules

    /**
     * This method calculates the influence on an island after it's conquered, and swap team towers if necessary
     * @param conqueringPlayer the player who conquered the island
     * @param island the island which got conquered
     * @param players all the players of the current game
     * @param charController the character card controller of the current game
     * @param cards all the usable character cards of the current game
     * @param playerController the player controller of the current game
     * @exception GameIsOverException when the game finishes
     */

    public void calculateInfluence(Player conqueringPlayer, Island_Tile island, ArrayList<Player> players, CharCardController charController,  ArrayList<CharacterCard> cards, PlayerController playerController) throws GameIsOverException {
        Team conqueringTeam=conqueringPlayer.getTeam();
        Team currentTeam=island.getTeam();
        EnumMap<Team,Integer> teamInfluence=new EnumMap<>(Team.class);
        EnumMap<Team,ArrayList<Colour>> teamColours=new EnumMap<>(Team.class);
        //Setup
        for (Team t: Team.values()) {
            teamInfluence.put(t,0);
            teamColours.put(t,playerController.getTeamColours(t,players));
        }
        //Tower influence
        if (charController==null) {
            if (currentTeam!=null) {
                teamInfluence.put(currentTeam,teamInfluence.get(currentTeam)+island.getTowers());
            }
        } else {
            //centaur negates tower influence of current island, knight adds 2 to initial influence to conquering player's team
            if (currentTeam!=null) {
                teamInfluence.put(currentTeam,teamInfluence.get(currentTeam)+(island.getTowers()*(charController.getActiveStatus(CharID.CENTAUR,cards)?0:1)));

            }
            teamInfluence.put(conqueringTeam,teamInfluence.get(conqueringTeam)+2*(charController.getActiveStatus(CharID.KNIGHT,cards)?1:0));
        }
        //Student Disk Influence
        for (Colour c: Colour.values()) {
            EnumMap<Colour,Boolean> disabledColours=null;
            if (cards!=null) {
                for (CharacterCard card : cards) {
                    if (card.getCharacter().equals(CharID.SHROOM_VENDOR)) {
                        disabledColours=card.getCharacter().getMemory(Boolean.class);
                    }
                }
            }
            if (disabledColours==null || !disabledColours.get(c)) {
                for (Team t: Team.values()) {
                    if (teamColours.get(t).contains(c)) {
                        teamInfluence.put(t,teamInfluence.get(t)+island.getContents().get(c));
                    }
                }
            }
        }
        Team maxTeam=Collections.max(teamInfluence.entrySet(), Map.Entry.comparingByValue()).getKey();
        int maxInfluence=teamInfluence.get(maxTeam);
        //If two or more teams have the same max influence previous island state is returned
        for (Team t: Team.values()) {
            if (t!=maxTeam && teamInfluence.get(t)==maxInfluence) {
                return;
            }
        }
        if (island.getTeam()==null) {
            if (conqueringPlayer.getSchoolBoard().getTowers()>0) {
                conqueringPlayer.getSchoolBoard().modifyTowers(-1);
            } else if (conqueringPlayer.getTeamMate()!=null && conqueringPlayer.getTeamMate().getSchoolBoard().getTowers()>0) {
                conqueringPlayer.getTeamMate().getSchoolBoard().modifyTowers(-1);
            } else {
                throw new GameIsOverException("Team "+conqueringTeam+" has run out of towers");
            }
            island.setTowers(1);
        } else {
            playerController.swapTeamTower(players,maxTeam,island.getTeam(),island.getTowers());
        }
        island.setTeam(maxTeam);
    }

    /**
     * This method is used to check if an island needs to be unified to its adjacent islands
     * @param islands all the islands present in the game
     * @param island the island who needs to be checked
     */
    public void checkAndMerge(ArrayList<Island_Tile> islands, Island_Tile island) {
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
    }
}
