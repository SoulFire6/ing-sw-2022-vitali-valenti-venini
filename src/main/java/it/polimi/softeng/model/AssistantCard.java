package it.polimi.softeng.model;


public class AssistantCard {
    private final String cardID;
    private final int turnValue;
    private final int motherNatureValue;

    public AssistantCard(String id, int turn, int motherNature) {
        this.cardID=id;
        this.turnValue=turn;
        this.motherNatureValue=motherNature;
    }
    public String getCardID() {
        return this.cardID;
    }
    public int getTurnValue() {
        return this.turnValue;
    }
    public int getMotherNatureValue() {
        return this.motherNatureValue;
    }
}
