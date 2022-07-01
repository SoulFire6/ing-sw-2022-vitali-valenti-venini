package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Island_Tile;
import it.polimi.softeng.model.Team;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * This class is part of the reduced model and represents its islands
 */
public class ReducedIsland implements Serializable {
    private final String id;
    private final EnumMap<Colour,Integer> contents;
    private final boolean motherNature;
    private final int towers;
    private final Team team;
    private final boolean noEntry;

    /**
     * This is the constructor. It initializes a reduced version of an Island_Tile
     * @param island the island to be reduced
     */
    public ReducedIsland(Island_Tile island) {
        this.id=island.getTileID();
        this.contents=island.getContents();
        this.motherNature=island.getMotherNature();
        this.towers=island.getTowers();
        this.team=island.getTeam();
        this.noEntry=island.getNoEntry();
    }

    /**
     * @return String the ID of this reduced island
     */
     public String getID() {
        return this.id;
     }

    /**
     * @return Colour EnumMap of Integer, the student disks contained in this reduced island
     */
    public EnumMap<Colour, Integer> getContents() {
        return contents.clone();
    }

    /**
     * @return true if mother nature is on this island, false otherwise
     */
    public boolean hasMotherNature() {
        return motherNature;
    }

    /**
     * @return int number of towers present on this island
     */
    public int getTowers() {
        return towers;
    }

    /**
     * @return Team that is in control of the island
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @return true if the island has the no entry tile, false otherwise
     */
    public boolean isNoEntry() {
        return noEntry;
    }
}
