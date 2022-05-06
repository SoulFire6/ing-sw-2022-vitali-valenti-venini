package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.CharacterCard;

import java.io.Serializable;

public class ReducedCharacterCard implements Serializable {
    private final String id;
    private final int cost;

    public ReducedCharacterCard(CharacterCard card) {
        this.id=card.getCardID();
        this.cost=card.getCost();
    }

    public String getId() {
        return this.id;
    }
    public int getCost() {
        return this.cost;
    }
}
