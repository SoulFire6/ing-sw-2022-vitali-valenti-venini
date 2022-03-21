package it.polimi.softeng.model;

public class Island extends Tile {
    private String IslandID;
    private Boolean motherNature;
    private Integer towers;
    private Team team;
    private Island next;
    private Island prev;

    public Island(String id) {
        this.IslandID=id;
        this.motherNature=false;
        this.towers=0;
        this.team=null;
        this.next=null;
        this.prev=null;
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
    public void calculateInfluence() {
        //TODO add when players and teams are introduced
    }
}