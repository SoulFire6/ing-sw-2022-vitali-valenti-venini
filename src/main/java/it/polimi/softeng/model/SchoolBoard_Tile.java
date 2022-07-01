package it.polimi.softeng.model;

import it.polimi.softeng.exceptions.MoveNotAllowedException;

import java.util.EnumMap;
import java.util.ArrayList;

/**
 * This class it's part of the model and represents its school board tiles
 */
public class SchoolBoard_Tile extends Tile{
    private final int maxEntranceSlots;
    private EnumMap<Colour,Integer> diningRoom;
    private final EnumMap<Colour,Boolean> professorTable;
    private int towers;
    private final int maxTowers;
    private final ArrayList<AssistantCard> hand;
    private AssistantCard lastUsedCard;
    private int coins;

    /**
     * This is the class constructor.
     * @param playerName String for player name
     * @param maxEntranceSlots int number that represents the maximum number of student disks that can be placed in the entrance
     * @param towers int number that represent the actual number of towers of the school board
     * @param maxTowers int number that represent the maximum number of towers that a school board can have
     * @param hand ArrayList of AssistantCard containing the assistant cards in the schoolBoard owner's hand
     * @param coins int number of coins that the schoolBoard owner's has
     */
    public SchoolBoard_Tile(String playerName, int maxEntranceSlots, int towers, int maxTowers,ArrayList<AssistantCard> hand, int coins) {
        this.setTileID(playerName+"'s_schoolBoard");
        this.maxEntranceSlots=maxEntranceSlots;
        this.diningRoom=Colour.genIntegerMap();
        this.professorTable=Colour.genBooleanMap();
        this.towers=towers;
        this.maxTowers=maxTowers;
        this.hand=hand;
        this.coins=coins;
    }

    /**
     * @return int number that represent the maximum number of towers that a school board can have
     */
    public int getMaxTowers() {
        return this.maxTowers;
    }

    /**
     * This method fills with the student disks taken from the bag the entrance of the schoolBoard
     * @param bag from which the student disks are drawn to fill the entrance
     */
    //The entrance is represented by the generic contents attribute
    public void fillEntrance(Bag_Tile bag) {
        this.setContents(bag.drawStudents(this.maxEntranceSlots));
    }

    /**
     * This method fills with the student disks taken from the specified cloud the entrance of the schoolBoard
     * @param cloud from which the student disks are drawn to fill the entrance
     * @throws MoveNotAllowedException if the number of student disks drawn from the cloud isn't equal to the missing disks in the entrance
     */
    public void fillEntrance(Cloud_Tile cloud) throws MoveNotAllowedException {
        if ((this.getFillAmount()+cloud.getFillAmount())==this.maxEntranceSlots) {
            EnumMap<Colour,Integer> newStudents=cloud.emptyTile(), entrance=this.getContents();
            for(Colour c: Colour.values()) {
                entrance.put(c,entrance.get(c)+newStudents.get(c));
            }
        } else {
            throw new MoveNotAllowedException("Error filling schoolBoard entrance: "+(this.getFillAmount()+cloud.getFillAmount())+">"+this.maxEntranceSlots);
        }
    }

    /**
     * @return Colour EnumMap of Integer containing the student disks stored in the dining room
     */
    public EnumMap<Colour,Integer> getDiningRoom() {
        return this.diningRoom;
    }

    /**
     * @param diningRoom new  ColourEnumMap of Integer that will replace the current diningRoom
     */
    public void setDiningRoom(EnumMap<Colour,Integer> diningRoom) {
        this.diningRoom=diningRoom;
    }

    /**
     * @return Colour EnumMap of Boolean, which contains every Colour defined in the enum associated with true if the schoolBoard possessor controls this professor, false otherwise
     * @see Colour
     */
    public EnumMap<Colour,Boolean> getProfessorTable() {
        return this.professorTable;
    }

    /**
     * @param c the colour of which you want to know the amount in the dining room
     * @return int the number of student disks of colour c present in the dining room
     */
    public int getDiningRoomAmount(Colour c) {
        return this.diningRoom.get(c);
    }

    /**
     * @param c the colour of the student that needs to be moved from the entrance to the dining room
     * @return true if the operation succeeded, false otherwise
     */
    //Returns boolean depending on success of moving student to dining room
    public boolean moveStudentToDiningRoom(Colour c) {
        if (this.diningRoom.get(c)<10) {
            this.removeColour(c,1);
            this.diningRoom.put(c,this.diningRoom.get(c)+1);
            return true;
        } else {
            //System.out.println("Error: max students of colour "+c+" reached in dining room");
            return false;
        }

    }

    /**
     * @param c colour of the student that needs to be moved from the entrance to an island
     * @param island the island where the student needs to be moved
     * @return boolean true if the operation succeeded, false otherwise
     */
    //Moves student to cloud
    public boolean moveStudentToIsland(Colour c, Island_Tile island) {
        if (this.removeColour(c,1)) {
            island.addColour(c,1);
            return true;
        } else {
            return false;
        }

    }

    /**
     * @param c the colour of which we want to know if the professor is owned on this schoolBoard
     * @return true if the professor of Colour c is present on this schoolBoard, false otherwise
     */
    public boolean getProfessor(Colour c) {
        return this.professorTable.get(c);
    }

    /**
     * @param c Colour of the professor pawn that needs to be set
     * @param value Boolean true if the professor is being owned, false otherwise
     */
    public void setProfessor(Colour c, Boolean value) {
        this.professorTable.put(c,value);
    }

    /**
     * @param num int number of towers to add (num can be below 0 when we want to decrement tower number)
     * @return true if the operation succeeded, false otherwise
     */
    //Alters tower number based on input, returns false if outside normal values
    public boolean modifyTowers(int num) {
        if (this.towers+num>=0 && this.towers+num<=this.maxTowers) {
            this.towers+=num;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return int number of towers present on the schoolBoard
     */
    public int getTowers() {
        return this.towers;
    }

    /**
     * @return ArrayList AssistantCard the hand of the schoolBoard owner
     */
    public ArrayList<AssistantCard> getHand() {
        return this.hand;
    }

    /**
     * This method allows to play an assistant card by its id, and then set the lastUsedCard attribute
     * @param id String identifier of the assistant card
     */
    public void playAssistantCard(String id) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getCardID().equals(id)) {
                this.lastUsedCard=hand.get(i);
                hand.remove(i);
                break;
            }
        }
    }

    /**
     * @param lastUsedCard AssistantCard that has been played last by the schoolBoard owner
     */
    public void setLastUsedCard(AssistantCard lastUsedCard) {
        this.lastUsedCard=lastUsedCard;
    }

    /**
     * @return AssistantCard the last played card by the schoolBoard owner
     */
    public AssistantCard getLastUsedCard() {
        return this.lastUsedCard;
    }

    /**
     * @return int number of coins available on the schoolBoard
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * @param value int to set the number of coins of the schoolBoard
     */
    public void setCoins(int value) {
        this.coins=value;
    }
}
