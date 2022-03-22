package it.polimi.softeng.model;

import java.util.EnumMap;

public class Bag_Tile extends Tile {
    public Bag_Tile(Integer num) {
        this.setTileID("Bag");
        this.addStudents(num);
    }
    public EnumMap<Colour,Integer> drawStudents(Integer num) {
        EnumMap<Colour,Integer> contents=this.getContents();
        EnumMap<Colour,Integer> res=Colour.genStudentMap();
        Colour c;
        while(num>0) {
            c=Colour.getRandomColour();
            if (contents.get(c)>0) {
                contents.put(c,contents.get(c)-1);
                res.put(c,res.get(c)+1);
                num--;
            }
            if (this.isEmpty()) {
                break;
            }
        }
        this.setContents(contents);
        return res;
    }
}