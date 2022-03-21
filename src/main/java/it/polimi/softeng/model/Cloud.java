package it.polimi.softeng.model;

import java.util.ArrayList;
import java.util.EnumMap;

public class Cloud {
    private String CloudID;
    private Integer maxSlots;
    private EnumMap<Colour,Integer> contents;

    public Cloud(String id, Integer maxSlots) {
        this.CloudID=id;
        this.maxSlots=maxSlots;
        this.contents=Colour.genStudentMap();
    }
    public static ArrayList<Cloud> genClouds(Integer playerNum) {
        ArrayList<Cloud> res=new ArrayList<>();
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
            res.add(new Cloud(String.valueOf(i),max));
        }
        return res;
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
