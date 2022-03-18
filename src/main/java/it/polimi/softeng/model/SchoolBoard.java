package it.polimi.softeng.model;

import java.util.*;

public class SchoolBoard {
    Integer maxEntranceSlots;
    EnumMap<Colour,Integer> entrance;
    EnumMap<Colour,Integer> diningRoom;
    EnumMap<Colour,Boolean> professorTable;
    Integer towers;
    Integer coins;
    ArrayList<AssistantCard> hand;

    public SchoolBoard() {
        this.maxEntranceSlots=7; //TODO set to 9 during 3 player game on setup
        this.entrance=Colour.genStudentMap();
        this.diningRoom=Colour.genStudentMap();
        this.professorTable=Colour.genProfessorMap();
        this.towers=8;
        this.coins=0;
        this.hand=AssistantCard.genHand();
    }
    public void fillEntrance(Bag b) {
        EnumMap<Colour,Integer> newStudents=b.drawStudents(this.maxEntranceSlots-Colour.currentlyFilledSlots(this.entrance));
        for(Colour c: Colour.values()) {
            this.entrance.put(c,this.entrance.get(c)+newStudents.get(c));
        }
    }
    public void modifyTowers(Integer num) {
        if (this.towers+num>=0 && this.towers+num<=8) {
            this.towers+=num;
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
