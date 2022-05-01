package it.polimi.softeng.model;

public class CharacterCard {
    private final String cardID;
    private int cost;

    public CharacterCard(String id,int cost) {
        this.cardID=id;
        this.cost=cost;
    }
    public String getCardID() {
        return this.cardID;
    }
    public int getCost() {
        return this.cost;
    }
    public void incrementCost() {
        this.cost+=1;
    }
}
