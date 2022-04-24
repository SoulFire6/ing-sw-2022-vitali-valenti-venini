package it.polimi.softeng.model;

import java.util.EnumMap;

public abstract class Tile {
    private String tileID;
    private EnumMap<Colour,Integer> contents;

    public Tile() {
        this.contents=Colour.genIntegerMap();
    }
    public Tile(int num) {
        this();
        this.addStudents(num);
    }
    public void setTileID(String id) {
        this.tileID = id;
    }
    public String getTileID() {
        return this.tileID;
    }
    public EnumMap<Colour, Integer> getContents() {
        return this.contents;
    }

    public void setContents(EnumMap<Colour,Integer> contents) {
        this.contents=contents;
    }
    //Adds a specified amount of one colour
    public void addColour(Colour c, int num) {
        this.contents.put(c,this.contents.get(c)+num);
    }
    //Removes a specified amount from one colour
    //Sets it to zero if the removed amount is higher
    //than the current amount
    public Boolean removeColour(Colour c, int num) {
        boolean value=false;
        //returns null if num is invalid
        if (num<0) {
            return null;
        }
        //returns true if the amount removed is equal to wanted, false otherwise
        if (this.contents.get(c)-num>=0) {
            value=true;
        }
        this.contents.put(c,Math.max(0,this.contents.get(c)-num));
        return value;
    }
    //Adds the same amount to every colour
    public void addStudents(int num) {
        for (Colour c: Colour.values()) {
            this.contents.put(c,this.contents.get(c)+num);
        }
    }
    //Adds the contents of an EnumMap into contents
    public void addStudents(EnumMap<Colour,Integer> added) {
        for (Colour c: Colour.values()) {
            this.addColour(c,added.get(c));
        }
    }
    //Empties tile of contents and returns their value
    public EnumMap<Colour,Integer> emptyTile() {
        EnumMap<Colour,Integer> res=this.contents;
        for(Colour c: Colour.values()) {
            this.contents.put(c,0);
        }
        return res;
    }
    //Returns how many students are in contents
    public int getFillAmount() {
        int sum=0;
        for (Colour c: Colour.values()) {
            sum+=this.contents.get(c);
        }
        return sum;
    }
    //Returns true if contents is empty (values for every colour key equal to 0)
    public boolean isEmpty() {
        Colour[] values=Colour.values();
        for (Colour value : values) {
            if (this.contents.get(value) != 0) {
                return false;
            }
        }
        return true;
    }
}

