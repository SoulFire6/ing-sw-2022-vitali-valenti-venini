package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.CharacterCard;

import java.io.Serializable;

public class ReducedCharacterCard implements Serializable {
    private final String id;
    private final int cost;

    private final String charID;
    private final String memoryType;
    private final Object memory;

    public ReducedCharacterCard(CharacterCard card) {
        this.id=card.getCardID();
        this.charID=card.getCharacter().name();
        this.cost=card.getCost();
        this.memoryType=card.getCharacter().getMemType().name();
        this.memory=card.getCharacter().getMemory();
    }

    public String getId() {
        return this.id;
    }
    public int getCost() {
        return this.cost;
    }

    public String getCharID() {
        return this.charID;
    }
    public String getMemoryType() {
        return this.memoryType;
    }
    public Object getMemory() {
        return this.memory;
    }
}
