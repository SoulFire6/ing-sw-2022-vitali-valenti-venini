package it.polimi.softeng;

import java.util.*;

public class SchoolBoard {
    EnumMap<Colour,Integer> entrance;
    EnumMap<Colour,Integer> diningRoom;
    EnumMap<Colour,Boolean> professorTable;
    Integer towers;
    Integer coins;
    ArrayList<AssistantCard> hand;

    public SchoolBoard() {
        this.entrance=Colour.genStudentMap();
        this.diningRoom=Colour.genStudentMap();
        this.professorTable=Colour.genProfessorMap();
        this.towers=8;
        this.coins=0;
        this.hand=AssistantCard.genHand();
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
