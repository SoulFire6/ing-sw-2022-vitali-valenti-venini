package it.polimi.softeng.model;


public class Island_Tile extends Tile {
    private boolean motherNature;
    private int towers;
    private Team team;
    private Island_Tile next;
    private Island_Tile prev;
    private boolean noEntry;

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
    public int getTowers() {
        return this.towers;
    }
    public void setTowers(int value) {
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
}