package it.polimi.softeng;

public class AssistantCard {
    Integer turnValue;
    Integer motherNatureValue;
    String cardID;

    public AssistantCard(Integer turn, Integer motherNature, String id) {
        this.turnValue=turn;
        this.motherNatureValue=motherNature;
        this.cardID=id;
    }
}
