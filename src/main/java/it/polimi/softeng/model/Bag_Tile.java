package it.polimi.softeng.model;

import java.util.EnumMap;

public class Bag_Tile extends Tile {
    public Bag_Tile(int num) {
        super(num);
        this.setTileID("Bag");
    }
    public EnumMap<Colour,Integer> drawStudents(int num) {
        EnumMap<Colour,Integer> contents=this.getContents();
        EnumMap<Colour,Integer> res=Colour.genIntegerMap();
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