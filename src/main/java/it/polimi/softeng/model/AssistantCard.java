package it.polimi.softeng.model;

import java.util.ArrayList;

public class AssistantCard {
    private String cardID;
    private Integer turnValue;
    private Integer motherNatureValue;

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
