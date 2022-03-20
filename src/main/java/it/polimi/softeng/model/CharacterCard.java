package it.polimi.softeng.model;

import java.util.ArrayList;

public class CharacterCard {
    private String cardID;
    private Integer cost;

    public CharacterCard(String id,Integer cost) {
        this.cardID=id;
        this.cost=cost;
    }
    public String getCardID() {
        return this.cardID;
    }
    public Integer getCost() {
        return this.cost;
    }
    public void incrementCost() {
        this.cost+=1;
    }
    public static ArrayList<CharacterCard> genCharacterCards() {
        //TODO: Add cards from csv file
        ArrayList<CharacterCard> res=new ArrayList<>();
        res.add(new CharacterCard("Example",0));
        return res;
    }
}
