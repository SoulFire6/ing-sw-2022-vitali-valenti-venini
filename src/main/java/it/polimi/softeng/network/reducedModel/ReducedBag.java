package it.polimi.softeng.network.reducedModel;

import it.polimi.softeng.model.Bag_Tile;
import it.polimi.softeng.model.Colour;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * This class is part of the reduced model and represents its bag
 */
public class ReducedBag implements Serializable {
    private final String id;
    private final EnumMap<Colour,Integer> contents;

    /**
     * This is the constructor. It initializes the reduced version of a Bag.
     * @param bag the bag that will get reduced
     */
    public ReducedBag(Bag_Tile bag) {
        this.id=bag.getTileID();
        this.contents=bag.getContents();
    }

    /**
     * @return String the id of this reduced bag
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return Colour EnumMap of Integer, the student discs contained in the bag
     */
    public EnumMap<Colour,Integer> getContents() {
        return this.contents.clone();
    }

}
