package it.polimi.softeng.model;

/**
 * This class it's part of the model and represents its island tiles
 */
public class Island_Tile extends Tile {
    private boolean motherNature;
    private int towers;
    private Team team;
    private Island_Tile next;
    private Island_Tile prev;
    private boolean noEntry;

    /**
     * This is the constructor of the Island_Tile class.
     * @param id String identifier of the island. It's in the form "Island_x", with x being the number of the island.
     */
    public Island_Tile(String id) {
        this.setTileID(id);
        this.motherNature=false;
        this.towers=0;
        this.team=null;
        this.next=null;
        this.prev=null;
        this.noEntry=false;
    }

    /**
     * @return Boolean true if mother nature is on this Island_Tile, false otherwise.
     */
    public Boolean getMotherNature() {
        return this.motherNature;
    }

    /**
     * @param value Boolean motherNature attribute will be set to this parameter value.
     */
    public void setMotherNature(Boolean value) {
        this.motherNature=value;
    }

    /**
     * @return int number of towers present on this Island
     */
    public int getTowers() {
        return this.towers;
    }

    /**
     * @param value int to set the number towers on this Island_Tile
     */
    public void setTowers(int value) {
        this.towers=value;
    }

    /**
     * @return Team that is in control of this Island_Tile, null if no team controls this island yet.
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * @param t Team that is being set to be in control of this island.
     */
    public void setTeam(Team t) {
        this.team=t;
    }

    /**
     * @return Island_Tile the next Island_Tile of the one this method is called on
     */
    public Island_Tile getNext() {
        return this.next;
    }

    /**
     * @param island the Island_Tile that is being set to be after the one this method is being called on
     */
    public void setNext(Island_Tile island) {
        this.next=island;
    }

    /**
     * @return Island_Tile the previous Island_Tile of the one this method is called on
     */
    public Island_Tile getPrev() {
        return this.prev;
    }

    /**
     * @param island the Island_Tile that is being set to be before the one this method is being called on
     */
    public void setPrev(Island_Tile island) {
        this.prev=island;
    }

    /**
     * @return Boolean true if on this island is present a no entry tile, false otherwise
     */
    public Boolean getNoEntry() {
        return this.noEntry;
    }

    /**
     * @param value true if is being added a no entry tile to the island, false if it's being removed
     */
    public void setNoEntry(Boolean value) {
        this.noEntry=value;
    }
}