package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.Bag_Tile;
import it.polimi.softeng.model.Colour;

import java.io.Serializable;
import java.util.EnumMap;

public class ReducedBag implements Serializable {
    private final String id;
    private final EnumMap<Colour,Integer> contents;

    public ReducedBag(Bag_Tile bag) {
        this.id=bag.getTileID();
        this.contents=bag.getContents();
    }

    public String getId() {
        return this.id;
    }
    public EnumMap<Colour,Integer> getContents() {
        return this.contents.clone();
    }

}
