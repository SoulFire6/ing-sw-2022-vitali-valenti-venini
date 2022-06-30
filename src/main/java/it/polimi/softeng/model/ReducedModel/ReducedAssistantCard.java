package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.AssistantCard;

import java.io.Serializable;

/**
 * This class is part of the reduced model and represents its assistant cards
 */
public class ReducedAssistantCard implements Serializable {
    private final String id;
    private final int turnValue;
    private final int motherNatureValue;

    /**
     * This is the constructor. It initializes the reduced version of an AssistantCard.
     * @param card the assistant card that will get reduced
     */
    public ReducedAssistantCard(AssistantCard card) {
        this.id=card.getCardID();
        this.turnValue=card.getTurnValue();
        this.motherNatureValue=card.getMotherNatureValue();
    }

    /**
     * @return String the id of this reduced assistant card
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return int the turn value of this reduced assistant card
     */
    public int getTurnValue() {
        return this.turnValue;
    }

    /**
     * @return int the mother nature value of this reduced assistant card
     */
    public int getMotherNatureValue() {
        return this.motherNatureValue;
    }
}
