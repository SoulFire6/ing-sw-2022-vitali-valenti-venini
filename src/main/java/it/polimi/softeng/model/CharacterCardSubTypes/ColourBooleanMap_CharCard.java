package it.polimi.softeng.model.CharacterCardSubTypes;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Colour;

import java.util.EnumMap;

public class ColourBooleanMap_CharCard extends CharacterCard {
    EnumMap<Colour,Boolean> memory;
    public ColourBooleanMap_CharCard(String id, int cost,EnumMap<Colour,Boolean> memory) {
        super(id,cost);
        this.memory=memory;
    }
    public EnumMap<Colour,Boolean> getMemory() {
        return this.memory;
    }
    public void setMemory(EnumMap<Colour,Boolean> memory) {
        this.memory=memory;
    }
    public void resetMemory() {
        for (Colour c: Colour.values()) {
            this.memory.put(c,false);
        }
    }
}
