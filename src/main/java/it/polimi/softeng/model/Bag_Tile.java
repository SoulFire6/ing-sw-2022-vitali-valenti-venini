package it.polimi.softeng.model;

import java.util.EnumMap;

/**
 * This class it's part of the model and represents the bag.
 */
public class Bag_Tile extends Tile {
    /**
     * This is the constructor. It initializes the bag.
     * @param num int number of student disks of each colour that the bag will get filled with
     */
    public Bag_Tile(int num) {
        super(num);
        this.setTileID("Bag");
    }

    /**
     * This method is used to randomly draw num students from the bag
     * @param num int number of students to randomly draw
     * @return EnumMap<Colour,Integer>, containing the drawn student disks (the EnumMap contains for each Colour the corresponding Integer number of drawn student disks)
     */
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