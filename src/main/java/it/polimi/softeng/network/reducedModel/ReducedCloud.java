package it.polimi.softeng.network.reducedModel;

import it.polimi.softeng.model.Cloud_Tile;
import it.polimi.softeng.model.Colour;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * This class is part of the reduced model and represents its clouds
 */
public class ReducedCloud implements Serializable {
    private final String id;
    private final EnumMap<Colour,Integer> contents;

    /**
     * This is the constructor. It initializes a reduced version of a Cloud_Tile
     * @param cloud the cloud tile that will get reduced
     */
    public ReducedCloud(Cloud_Tile cloud) {
        this.id=cloud.getTileID();
        this.contents=cloud.getContents();
    }

    /**
     * @return String id of the cloud
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return colour EnumMap of Integer, that represents the student disks content of the cloud
     */
    public EnumMap<Colour,Integer> getContents() {
        return this.contents.clone();
    }
}
