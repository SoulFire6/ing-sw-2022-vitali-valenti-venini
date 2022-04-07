package it.polimi.softeng.model;

import java.util.ArrayList;
import java.util.Random;

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
    public static ArrayList<Island_Tile> genIslands(int num) {
        ArrayList<Island_Tile> islands=new ArrayList<>();
        ArrayList<String> initialisedIslands=new ArrayList<>();
        Bag_Tile bag=new Bag_Tile((int)Math.ceil((num-2.0)/Colour.values().length));
        Island_Tile head=new Island_Tile("Island_1");
        Island_Tile curr=head;
        islands.add(head);
        for (int i=1; i<num; i++) {
            islands.add(curr.next=new Island_Tile("Island_"+(i+1)));
            curr.next.prev=curr;
            curr=curr.next;
        }
        curr.next=head;
        head.prev=curr;
        Random rand=new Random();
        int randIsland=Math.max(1,rand.nextInt(num));
        int opposite=((num/2)+randIsland);
        if (opposite>num) {
            opposite%=num;
        }
        boolean foundMNISLE=false;
        boolean foundOpposite=false;
        for (Island_Tile island: islands) {
            if (island.getTileID().equals("Island_"+randIsland)) {
                island.setMotherNature(true);
                initialisedIslands.add(island.getTileID());
                foundMNISLE=true;
            }
            if (island.getTileID().equals("Island_"+opposite)) {
                initialisedIslands.add(island.getTileID());
                foundOpposite=true;
            }
            if (foundMNISLE && foundOpposite) {
                break;
            }
        }
        //Puts a student on every island except the one with mother nature and the one opposite
        for (Island_Tile island: islands) {
            if (!initialisedIslands.contains(island.getTileID())) {
                island.setContents(bag.drawStudents(1));
                initialisedIslands.add(island.getTileID());
            }
        }
        return islands;
    }
}