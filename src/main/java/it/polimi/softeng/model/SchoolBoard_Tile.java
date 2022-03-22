package it.polimi.softeng.model;

import java.util.EnumMap;
import java.util.ArrayList;

public class SchoolBoard_Tile extends Tile{
    private Integer maxEntranceSlots;
    private EnumMap<Colour,Integer> entrance;
    private EnumMap<Colour,Integer> diningRoom;
    private EnumMap<Colour,Boolean> professorTable;
    private Integer towers;
    private Integer coins;
    private ArrayList<AssistantCard> hand;

    public SchoolBoard_Tile(Integer maxEntranceSlots, Integer towers, Integer coins) {
        this.setTileID("");
        this.maxEntranceSlots=maxEntranceSlots;
        this.diningRoom=Colour.genStudentMap();
        this.professorTable=Colour.genProfessorMap();
        this.towers=towers;
        this.coins=coins;
        this.hand=AssistantCard.genHand();
    }
    //The entrance is represented by the generic contents attribute
    public void fillEntrance(Cloud_Tile cloud) {
        EnumMap<Colour,Integer> entrance=this.getContents();
        EnumMap<Colour,Integer> newStudents=cloud.getContents();
        for(Colour c: Colour.values()) {
            entrance.put(c,entrance.get(c)+newStudents.get(c));
            newStudents.put(c,0);
        }
        this.setContents(entrance);
        cloud.setContents(newStudents);
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
    public void moveStudentToCloud(Colour c, Cloud_Tile cloud) {
        this.removeColour(c,1);
        cloud.addColour(c,1);
    }
    //Alters tower number based on input, returns false if outside normal values
    public boolean modifyTowers(Integer num) {
        if (this.towers+num>=0 && this.towers+num<=8) {
            this.towers+=num;
            return true;
        } else {
            return false;
        }
    }
    public AssistantCard playAssistantCard(String id) {
        AssistantCard res=null;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getCardID().equals(id)) {
                res=hand.get(i);
                hand.remove(i);
                break;
            }
        }
        return res;
    }
}
