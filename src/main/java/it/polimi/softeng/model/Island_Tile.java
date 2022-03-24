package it.polimi.softeng.model;

import java.util.ArrayList;

public class Island_Tile extends Tile {
    private Boolean motherNature;
    private Integer towers;
    private Team team;
    private Island_Tile next;
    private Island_Tile prev;
    private Boolean noEntry;

    public Island_Tile(String id) {
        this.setTileID(id);
        this.motherNature=false;
        this.towers=0;
        this.team=null;
        this.next=null;
        this.prev=null;
        this.noEntry=false;
    }
    public Boolean getMotherNature() {
        return this.motherNature;
    }
    public void setMotherNature(Boolean value) {
        this.motherNature=value;
    }
    public Integer getTowers() {
        return this.towers;
    }
    public void setTowers(Integer value) {
        this.towers=value;
    }
    public Team getTeam() {
        return this.team;
    }
    public void setTeam(Team t) {
        this.team=t;
    }
    public Island_Tile getNext() {
        return this.next;
    }
    public void setNext(Island_Tile island) {
        this.next=island;
    }
    public Island_Tile getPrev() {
        return this.prev;
    }
    public void setPrev(Island_Tile island) {
        this.prev=island;
    }
    public Boolean getNoEntry() {
        return this.noEntry;
    }
    public void setNoEntry(Boolean value) {
        this.noEntry=value;
    }
    public static ArrayList<Island_Tile> genIslands(Integer num) {
        //TODO: initialise mother nature and students on separate isles randomly
        ArrayList<Island_Tile> islands=new ArrayList<>();
        for (int i=0; i<num; i++) {

            islands.add(new Island_Tile("Island_"+(i+1)));
        }
        for (int i=0; i<num-1; i++) {
            islands.get(i).setNext(islands.get(i+1));
            islands.get(i+1).setPrev(islands.get(i));
        }
        islands.get(0).setPrev(islands.get(num-1));
        islands.get(num-1).setNext(islands.get(0));
        return islands;
    }
}