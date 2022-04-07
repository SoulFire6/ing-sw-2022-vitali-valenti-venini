package it.polimi.softeng.model;

import java.util.ArrayList;
import java.util.EnumMap;

public class Cloud_Tile extends Tile {
    private int maxSlots;

    public Cloud_Tile(String id, int max) {
        this.setTileID(id);
        this.maxSlots=max;
    }
    //returns the maxSlots attribute
    public int getMaxSlots() {
        return this.maxSlots;
    }
    //sets the maxSlots attribute
    public void setMaxSlots(int maxSlots) {
        this.maxSlots=maxSlots;
    }
    //Fills cloud by drawing from the bag
    public void fillCloud(Bag_Tile b) {
        EnumMap<Colour,Integer> contents=this.getContents();
        EnumMap<Colour,Integer> cloudContents=b.drawStudents(this.maxSlots-this.getFillAmount());
        for(Colour c: Colour.values()) {
            contents.put(c,contents.get(c)+cloudContents.get(c));
        }
        this.setContents(contents);
    }
    //Generates clouds for the game class
    public static ArrayList<Cloud_Tile> genClouds(int num, int max) {
        ArrayList<Cloud_Tile> res=new ArrayList<>();
        for (int i=0; i<num; i++) {
            res.add(new Cloud_Tile("Cloud_"+(i+1),max));
        }
        return res;
    }
}