package it.polimi.softeng.model;

import java.util.ArrayList;
import java.util.EnumMap;

public class Cloud_Tile extends Tile {
    private Integer maxSlots;

    public Cloud_Tile(String id, Integer max) {
        this.setTileID(id);
        this.maxSlots=max;
    }
    //Generates 2-4 clouds based on player number, with corresponding maxSlots value
    public static ArrayList<Cloud_Tile> genClouds(Integer playerNum) {
        ArrayList<Cloud_Tile> res=new ArrayList<>();
        Integer num;
        Integer max;
        switch (playerNum) {
            case 2:
                num=2;
                max=3;
                break;
            case 3:
                num=3;
                max=4;
                break;
            case 4:
                num=4;
                max=3;
                break;
            default:
                num=2;
                max=3;
                break;
        }
        for (int i=0; i<num; i++) {
            res.add(new Cloud_Tile(String.valueOf(i),max));
        }
        return res;
    }
    public void fillCloud(Bag_Tile b) {
        EnumMap<Colour,Integer> contents=this.getContents();
        EnumMap<Colour,Integer> cloudContents=b.drawStudents(this.maxSlots-this.getFillAmount());
        for(Colour c: Colour.values()) {
            contents.put(c,contents.get(c)+cloudContents.get(c));
        }
        this.setContents(contents);
    }
}