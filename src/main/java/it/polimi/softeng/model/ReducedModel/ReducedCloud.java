package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.Cloud_Tile;
import it.polimi.softeng.model.Colour;

import java.io.Serializable;
import java.util.EnumMap;

public class ReducedCloud implements Serializable {
    private final String id;
    private final EnumMap<Colour,Integer> contents;

    public ReducedCloud(Cloud_Tile cloud) {
        this.id=cloud.getTileID();
        this.contents=cloud.getContents();
    }

    public String getId() {
        return this.id;
    }
    public EnumMap<Colour,Integer> getContents() {
        return this.contents.clone();
    }
}
