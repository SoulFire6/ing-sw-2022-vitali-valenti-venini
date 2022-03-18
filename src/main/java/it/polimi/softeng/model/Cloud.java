package it.polimi.softeng.model;

import java.util.EnumMap;

public class Cloud {
    String CloudID;
    Integer maxSlots;
    EnumMap<Colour,Integer> contents;

    public Cloud(String id) {
        this.CloudID=id;
        this.maxSlots=3; //TODO changes to 4 with 3 players during setup
        this.contents=Colour.genStudentMap();
    }

    public EnumMap<Colour,Integer> emptyCloud() {
        EnumMap<Colour,Integer> res=this.contents;
        for(Colour c: Colour.values()) {
            this.contents.put(c,0);
        }
        return res;
    }
    public void fillCloud(Bag b) {
        EnumMap<Colour,Integer> cloudContents=b.drawStudents(this.maxSlots-Colour.currentlyFilledSlots(this.contents));
        for(Colour c: Colour.values()) {
            this.contents.put(c,this.contents.get(c)+cloudContents.get(c));
        }
    }
}
