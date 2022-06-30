package it.polimi.softeng.model;

/**
 * This class it's part of the model and represents the assistant cards of the game.
 */
public class AssistantCard {
    private final String cardID;
    private final int turnValue;
    private final int motherNatureValue;

    /**
     * This is the constructor. It initializes an assistant card.
     * @param id String identifier of the assistant card
     * @param turn int turn value of the assistant card, used to decide turn orders
     * @param motherNature int value representing mother nature max. allowed moves
     */
    public AssistantCard(String id, int turn, int motherNature) {
        this.cardID=id;
        this.turnValue=turn;
        this.motherNatureValue=motherNature;
    }

    /**
     * @return String id of this assistant card
     */
    public String getCardID() {
        return this.cardID;
    }

    /**
     * @return int turn value of this assistant card
     */
    public int getTurnValue() {
        return this.turnValue;
    }

    /**
     * @return int mother nature value of this assistant card
     */
    public int getMotherNatureValue() {
        return this.motherNatureValue;
    }
}
