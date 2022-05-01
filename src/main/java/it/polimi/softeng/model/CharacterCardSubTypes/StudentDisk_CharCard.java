package it.polimi.softeng.model.CharacterCardSubTypes;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Colour;

import java.util.EnumMap;

public class StudentDisk_CharCard extends CharacterCard {
    EnumMap<Colour,Integer> memory;
    public StudentDisk_CharCard(String id, int cost, EnumMap<Colour,Integer> memory) {
        super(id,cost);
        this.memory=memory;
    }
    public EnumMap<Colour,Integer> getMemory() {
        return this.memory;
    }
    public void setMemory(EnumMap<Colour,Integer> memoru) {
        this.memory=memoru;
    }
}
