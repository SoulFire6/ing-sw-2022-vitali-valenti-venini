package it.polimi.softeng;

import java.util.*;

public class AssistantCard {
    String cardID;
    Integer turnValue;
    Integer motherNatureValue;

    public AssistantCard(String id, Integer turn, Integer motherNature) {
        this.cardID=id;
        this.turnValue=turn;
        this.motherNatureValue=motherNature;
    }
    public String getCardID() {
        return this.cardID;
    }
    public static ArrayList<AssistantCard> genHand() {
        //TODO: Add cards from csv file
        ArrayList<AssistantCard> res=new ArrayList<>();
        res.add(new AssistantCard("Example",0,0));
        return res;
    }
}
