package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Island_Tile;
import it.polimi.softeng.model.Team;

import java.io.Serializable;
import java.util.EnumMap;

public class ReducedIsland implements Serializable {
    private final String id;
    private final EnumMap<Colour,Integer> contents;
    private final boolean motherNature;
    private final int towers;
    private final Team team;
    private final boolean noEntry;

    public ReducedIsland(Island_Tile island) {
        this.id=island.getTileID();
        this.contents=island.getContents();
        this.motherNature=island.getMotherNature();
        this.towers=island.getTowers();
        this.team=island.getTeam();
        this.noEntry=island.getNoEntry();
    }

     public String getID() {
        return this.id;
     }
    public EnumMap<Colour, Integer> getContents() {
        return contents.clone();
    }
    public boolean hasMotherNature() {
        return motherNature;
    }
    public int getTowers() {
        return towers;
    }
    public Team getTeam() {
        return team;
    }
    public boolean isNoEntry() {
        return noEntry;
    }
}
