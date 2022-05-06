package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.AssistantCard;

import java.io.Serializable;

public class ReducedAssistantCard implements Serializable {
    private final String id;
    private final int turnValue;
    private final int motherNatureValue;

    public ReducedAssistantCard(AssistantCard card) {
        this.id=card.getCardID();
        this.turnValue=card.getTurnValue();
        this.motherNatureValue=card.getMotherNatureValue();
    }

    public String getId() {
        return this.id;
    }
    public int getTurnValue() {
        return this.turnValue;
    }
    public int getMotherNatureValue() {
        return this.motherNatureValue;
    }
}
