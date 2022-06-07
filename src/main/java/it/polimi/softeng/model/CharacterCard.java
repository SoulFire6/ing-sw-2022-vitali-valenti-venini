package it.polimi.softeng.model;

public class CharacterCard {
    private final String cardID;
    private int cost;
    private final CharID character;

    private boolean active;

    public CharacterCard(String cardID,int cost,CharID character) {
        this.cardID=cardID;
        this.cost=cost;
        this.character=character;
        this.active=false;
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

    public CharID getCharacter() {
        return this.character;
    }
    public void setActive(boolean active) {
        this.active=active;
    }
    public boolean isActive() {
        return this.active;
    }

}
