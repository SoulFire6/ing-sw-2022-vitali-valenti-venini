package it.polimi.softeng.model;

import java.util.EnumMap;

public class Island {
    String IslandID;
    Boolean motherNature;
    Integer towers;
    Team team;
    Island next;
    Island prev;
    EnumMap<Colour,Integer> contents;

    public Island(String id) {
        this.IslandID=id;
        this.motherNature=false;
        this.towers=0;
        this.team=null;
        this.next=null;
        this.prev=null;
        this.contents=Colour.genStudentMap();
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
    public EnumMap<Colour,Integer>  getContents() {
        return this.contents;
    }
    public void addStudent(Colour c) {
        this.contents.put(c,this.contents.get(c)+1);
    }
    public void calculateInfluence() {
        //TODO add when players and teams are introduced
    }
}
