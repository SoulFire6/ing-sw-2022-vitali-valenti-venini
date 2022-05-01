package it.polimi.softeng.model.CharacterCardSubTypes;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Player;

import java.util.EnumMap;

public class ColourPlayerMap_CharCard extends CharacterCard {
    EnumMap<Colour, Player> memory;
    public ColourPlayerMap_CharCard(String id, int cost,EnumMap<Colour,Player> memory) {
        super(id,cost);
        this.memory=memory;
    }
    public EnumMap<Colour,Player> getMemory() {
        return this.memory;
    }
    public void setMemory(EnumMap<Colour,Player> memory) {
        this.memory=memory;
    }
    public void resetMemory() {
        for (Colour c: Colour.values()) {
            this.memory.put(c,null);
        }
    }
}
