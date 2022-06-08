package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Colour;

import java.io.Serializable;
import java.util.EnumMap;

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
        this.memory=card.getCharacter().getUncastedMemory();
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
    public Integer getMemory() {
        try {
            return (Integer) this.memory;
        } catch (ClassCastException cce) {
            return null;
        }
    }
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
        } catch (ClassCastException cce) {
            return null;
        }
        catch (NullPointerException npe) {
            System.out.println(valueClass);
            npe.printStackTrace();
            return null;
        }
    }
}
