package it.polimi.softeng.model;

import java.util.EnumMap;
import java.util.ArrayList;

public class SchoolBoard_Tile extends Tile{
    private int maxEntranceSlots;
    private EnumMap<Colour,Integer> diningRoom;
    private EnumMap<Colour,Boolean> professorTable;
    private int towers;
    private int maxTowers;
    private ArrayList<AssistantCard> hand;
    private AssistantCard lastUsedCard;
    private int coins;

    public SchoolBoard_Tile(String playerName, int maxEntranceSlots, int towers, int maxTowers,ArrayList<AssistantCard> hand, int coins) {
        this.setTileID(playerName+"'s_schoolboard");
        this.maxEntranceSlots=maxEntranceSlots;
        this.diningRoom=Colour.genIntegerMap();
        this.professorTable=Colour.genBooleanMap();
        this.towers=towers;
        this.maxTowers=maxTowers;
        this.hand=hand;
        this.coins=coins;
    }
    public int getMaxExntranceSlots() {
        return this.maxEntranceSlots;
    }
    public int getMaxTowers() {
        return this.maxTowers;
    }
    //The entrance is represented by the generic contents attribute
    public void fillEntrance(Bag_Tile bag) {
        this.setContents(bag.drawStudents(this.maxEntranceSlots));
    }
    public void fillEntrance(Cloud_Tile cloud) {
        EnumMap<Colour,Integer> entrance=this.getContents();
        EnumMap<Colour,Integer> newStudents=cloud.getContents();
        if ((this.getFillAmount()+cloud.getFillAmount())==this.maxEntranceSlots) {
            for(Colour c: Colour.values()) {
                entrance.put(c,entrance.get(c)+newStudents.get(c));
                newStudents.put(c,0);
            }
            this.setContents(entrance);
            cloud.setContents(newStudents);
        } else {
            System.out.println("Error filling schoolboard entrance");
        }
    }
    public int getDiningRoomAmount(Colour c) {
        return this.diningRoom.get(c);
    }
    //Returns boolean depending on success of moving student to dining room
    public boolean moveStudentToDiningRoom(Colour c) {
        if (this.diningRoom.get(c)<10) {
            this.removeColour(c,1);
            this.diningRoom.put(c,this.diningRoom.get(c)+1);
            return true;
        } else {
            System.out.println("Error: max students of colour "+c+" reached in dining room");
            return false;
        }

    }
    //Moves student to cloud
    public boolean moveStudentToIsland(Colour c, Island_Tile island) {
        if (this.removeColour(c,1)) {
            island.addColour(c,1);
            return true;
        } else {
            return false;
        }

    }
    public boolean getProfessor(Colour c) {
        return this.professorTable.get(c);
    }
    public void setProfessor(Colour c, Boolean value) {
        this.professorTable.put(c,value);
    }
    //Alters tower number based on input, returns false if outside normal values
    public boolean modifyTowers(int num) {
        if (this.towers+num>=0 && this.towers+num<=this.maxTowers) {
            this.towers+=num;
            return true;
        } else {
            return false;
        }
    }
    public int getTowers() {
        return this.towers;
    }
    public ArrayList<AssistantCard> getHand() {
        return this.hand;
    }
    public void playAssistantCard(String id) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getCardID().equals(id)) {
                this.lastUsedCard=hand.get(i);
                hand.remove(i);
                break;
            }
        }
    }
    public AssistantCard getLastUsedCard() {
        return this.lastUsedCard;
    }
    public int getCoins() {
        return this.coins;
    }
    public void setCoins(int value) {
        this.coins=value;
    }
}
