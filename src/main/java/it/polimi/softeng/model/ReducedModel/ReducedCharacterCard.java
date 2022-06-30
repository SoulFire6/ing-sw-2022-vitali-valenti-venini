package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Colour;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * This class is part of the reduced model and represents its character cards
 */
public class ReducedCharacterCard implements Serializable {
    private final String id;
    private final int cost;

    private final String charID;
    private final String memoryType;
    private final Object memory;

    /**
     * This is the constructor. It initializes the reduced version of a CharacterCard.
     * @param card the character card that will get reduced
     */
    public ReducedCharacterCard(CharacterCard card) {
        this.id=card.getCardID();
        this.charID=card.getCharacter().name();
        this.cost=card.getCost();
        this.memoryType=card.getCharacter().getMemType().name();
        this.memory=card.getCharacter().getUncastedMemory();
    }

    /**
     * @return String the id of this reduced character card
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return int the cost of this reduced character card
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * @return the ID of this reduced character card
     */
    public String getCharID() {
        return this.charID;
    }

    /**
     * @return the memory type of this reduced character card
     */
    public String getMemoryType() {
        return this.memoryType;
    }

    /**
     * @return Integer the memory of the character card
     */
    public Integer getMemory() {
        try {
            return (Integer) this.memory;
        } catch (ClassCastException cce) {
            return null;
        }
    }

    /**
     * @return the memory of the character card
     */

    public <V> EnumMap<Colour, V> getMemory(Class<V> valueClass) {
        try {
            @SuppressWarnings("unchecked")
            EnumMap<Colour, V> testMap = (EnumMap<Colour, V>) memory;

            for (Colour c : Colour.values()) {
                if (!testMap.get(c).getClass().equals(valueClass)) {
                    throw new ClassCastException("Value was not of class " + valueClass);
                }
            }
            return testMap;
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
