package it.polimi.softeng.model.CharacterCardSubTypes;

import it.polimi.softeng.model.CharacterCard;

public class Int_CharCard extends CharacterCard {
    int memory;
    public Int_CharCard(String id, int cost,int memory) {
        super(id,cost);
        this.memory=memory;
    }
    public int getMemory() {
        return this.memory;
    }
    public void setMemory(int memory) {
        this.memory=memory;
    }


}
