package it.polimi.softeng;

import java.util.*;

public class AssistantCard {
    Integer turnValue;
    Integer motherNatureValue;
    String cardID;

    public AssistantCard(Integer turn, Integer motherNature, String id) {
        this.turnValue=turn;
        this.motherNatureValue=motherNature;
        this.cardID=id;
    }
    public String getCardID() {
        return this.cardID;
    }
    public static ArrayList<AssistantCard> genHand() {
        //TODO: Add cards from csv file
        ArrayList<AssistantCard> res=new ArrayList<>();
        res.add(new AssistantCard(0,0,"Example"));
        return res;
    }
}
