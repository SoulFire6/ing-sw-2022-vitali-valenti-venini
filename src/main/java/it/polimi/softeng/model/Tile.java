package it.polimi.softeng.model;

import java.util.EnumMap;

/**
 * This class it's part of the model, and it's an abstract class, representing an abstract Tile.
 * This is the parent class of Bag_Tile, Cloud_Tile, Island_Tile and SchoolBoard_Tile
 */
public abstract class Tile {
    private String tileID;
    private EnumMap<Colour,Integer> contents;

    /**
     *This is the default constructor of the class. It defines the Tile content as an IntegerMap.
     */
    public Tile() {
        this.contents=Colour.genIntegerMap();
    }

    /**
     * This is a constructor of the class. It fills the Tile content with an equal number of student disks for each colour.
     * @param num the number of student disks of each colour to fill the content with
     */
    public Tile(int num) {
        this();
        this.addStudents(num);
    }

    /**
     * @param id String identifier of the Tile to set
     */
    public void setTileID(String id) {
        this.tileID = id;
    }

    /**
     * @return String identifier of the Tile
     */
    public String getTileID() {
        return this.tileID;
    }

    /**
     * @return Colour EnumMap of Integer, the content of the Tile
     */
    public EnumMap<Colour, Integer> getContents() {
        return this.contents;
    }

    /**
     * @param contents the Colour EnumMap of Integer to set the contents to, representing each student disk colour with the corresponding number to be set in the Tile contents attribute
     */
    public void setContents(EnumMap<Colour,Integer> contents) {
        this.contents=contents;
    }

    /**
     * This method add a number of student disks of the same colour to contents
     * @param c Colour of the student disks to add
     * @param num int number of the student disks to add
     */
    public void addColour(Colour c, int num) {
        this.contents.put(c,this.contents.get(c)+num);
    }

    /**
     * This method removes a number of student disks of the same colour from contents. If num does exceed the number of students in contents, all students of this colour are removed from contents.
     * @param c Colour of the student disks to be removed from the Tile contents attribute
     * @param num number of student disks of colour c to be removed from contents
     * @return true if all desired student disks have been removed, false if num exceeds the number of student disks present, null if num below 0
     */
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

    /**
     * This method adds same number of all student disks to contents
     * @param num the number of students to add
     */
    //Adds the same amount to every colour
    public void addStudents(int num) {
        for (Colour c: Colour.values()) {
            this.contents.put(c,this.contents.get(c)+num);
        }
    }

    /**
     * This method adds the content of a Colour EnumMap of Integer into contents
     * @param added the Colour EnumMap of Integer, to add
     */
    //Adds the contents of an EnumMap into contents
    public void addStudents(EnumMap<Colour,Integer> added) {
        for (Colour c: Colour.values()) {
            this.addColour(c,added.get(c));
        }
    }

    /**
     * This method empties contents and returns its previous value
     * @return a cloned value of contents before being emptied
     */
    //Empties tile of contents and returns their value
    public EnumMap<Colour,Integer> emptyTile() {
        EnumMap<Colour,Integer> res=this.contents.clone();
        for(Colour c: Colour.values()) {
            this.contents.put(c,0);
        }
        return res;
    }

    /**
     * @return int total number of students present in contents, without separating them into different colours.
     */
    //Returns how many students are in contents
    public int getFillAmount() {
        int sum=0;
        for (Colour c: Colour.values()) {
            sum+=this.contents.get(c);
        }
        return sum;
    }

    /**
     * @return boolean true if contents is empty, false otherwise
     */
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

