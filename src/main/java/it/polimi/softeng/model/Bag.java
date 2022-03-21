package it.polimi.softeng.model;

import java.util.EnumMap;

public class Bag {
    private EnumMap<Colour,Integer> contents;

    public Bag() {
        this.contents=Colour.genStudentMap();
    }
    public Bag(Integer num) {
        this.contents=Colour.genStudentMap();
        for (Colour c: Colour.values()) {
            this.contents.put(c,num);
        }
    }
    public EnumMap<Colour,Integer> getContents() {
        return this.contents;
    }
    public void addColourToBag(Colour c,Integer i) {
        this.contents.put(c,this.contents.get(c)+i);
    }
    public boolean isEmpty() {
        Colour[] values=Colour.values();
        for (Colour value : values) {
            if (this.contents.get(value) != 0) {
                return false;
            }
        }
        return true;
    }
    public EnumMap<Colour,Integer> drawStudents(Integer num) {
        EnumMap<Colour,Integer> res=Colour.genStudentMap();
        Colour c;
        while(num>0) {
            c=Colour.getRandomColour();
            if (this.contents.get(c)>0) {
                this.contents.put(c,this.contents.get(c)-1);
                res.put(c,res.get(c)+1);
                num--;
            }
            if (this.isEmpty()) {
                //System.out.println("Bag is now empty");
                break;
            }
        }
        return res;
    }
}
