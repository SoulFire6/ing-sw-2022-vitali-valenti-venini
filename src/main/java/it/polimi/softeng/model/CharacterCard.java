package it.polimi.softeng.model;

/**
 * This class it's part of the model and represents its character cards.
 */
public class CharacterCard {
    private final String cardID;
    private int cost;
    private final CharID character;

    private boolean active;

    /**
     * This is the constructor. It initializes a character card.
     * @param cardID String id of the character card
     * @param cost int activation cost (in coins) of the character card
     * @param character CharID type of the character card
     * @see CharID
     */
    public CharacterCard(String cardID,int cost,CharID character) {
        this.cardID=cardID;
        this.cost=cost;
        this.character=character;
        this.active=false;
    }

    /**
     * @return String the character card id
     */
    public String getCardID() {
        return this.cardID;
    }

    /**
     * @return int the cost (in coins) of the character card
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * increments the cost of the character card by 1 coin
     */
    public void incrementCost() {
        this.cost+=1;
    }

    /**
     * @return return the CharID of this character card
     * @see CharID
     *
     */
    public CharID getCharacter() {
        return this.character;
    }

    /**
     * @param active boolean true if the card is being activated, false if the card is being disabled
     */
    public void setActive(boolean active) {
        this.active=active;
    }

    /**
     * @return boolean true if the card is active, false otherwise
     */
    public boolean isActive() {
        return this.active;
    }

}
