package it.polimi.softeng.model;

import java.util.EnumMap;
/**
 * This class it's part of the model and represents its cloud tiles.
 */
public class Cloud_Tile extends Tile {
    private int maxSlots;

    /**
     * This is the constructor of the class. It does initialize a cloud tile.
     * @param id String identifier for the cloud tile.
     * @param max int maximum number of student disks that this cloud can store.
     */
    public Cloud_Tile(String id, int max) {
        this.setTileID(id);
        this.maxSlots=max;
    }

    /**
     * @return int maximum number of student disks that this cloud can store.
     */
    //returns the maxSlots attribute
    public int getMaxSlots() {
        return this.maxSlots;
    }

    /**
     * @param maxSlots int maximum number of student disks that this cloud can store.
     */
    //sets the maxSlots attribute
    public void setMaxSlots(int maxSlots) {
        this.maxSlots=maxSlots;
    }

    /**
     * This method fills the cloud tile taking the student disks from the bag and set the new content of the cloud tile.
     * @param b the bag from which the student disks are drawn
     */
    //Fills cloud by drawing from the bag
    public void fillCloud(Bag_Tile b) {
        EnumMap<Colour,Integer> contents=this.getContents();
        EnumMap<Colour,Integer> cloudContents=b.drawStudents(this.maxSlots-this.getFillAmount());
        for(Colour c: Colour.values()) {
            contents.put(c,contents.get(c)+cloudContents.get(c));
        }
        this.setContents(contents);
    }
}