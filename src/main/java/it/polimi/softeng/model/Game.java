package it.polimi.softeng.model;

import java.util.ArrayList;

public class Game {
    private final String gameID;
    private final int diningRoomMaxCapacity;
    private final int playerNum;
    private final ArrayList<Player> players;
    private ArrayList<Team> teams;
    private Bag_Tile bag;
    private ArrayList<Cloud_Tile> clouds;
    private ArrayList<Island_Tile> islands;
    private boolean expertMode;
    private int coins;
    private ArrayList<CharacterCard> characterCards;

    //Normal game constructor
    public Game(String gameID, ArrayList<Player> players, int bagFill, int cloudNum, int cloudMax, int islandNum) {
        this.gameID=gameID;
        this.playerNum=players.size();
        this.players=players;
        this.teams=Team.getTeams(this.players);
        this.bag=new Bag_Tile(bagFill);
        this.clouds=Cloud_Tile.genClouds(cloudNum,cloudMax);
        this.islands=Island_Tile.genIslands(islandNum);
        this.expertMode=false;
        this.diningRoomMaxCapacity =10;
    }

    //Expert game constructor
    public Game(String gameID, ArrayList<Player> players,int bagFill, int cloudNum, int cloudMax,int islandNum, int coins, ArrayList<CharacterCard> characterCards) {
        this(gameID,players,bagFill,cloudNum,cloudMax,islandNum);
        this.expertMode=true;
        this.coins=coins;
        this.characterCards=characterCards;
    }
    public String getGameID() {
        return this.gameID;
    }
    public int getPlayerNum() {
        return this.playerNum;
    }
    public ArrayList<Player> getPlayers() {
        return this.players;
    }
    public ArrayList<Team> getTeams() {
        return this.teams;
    }
    public Bag_Tile getBag() {
        return this.bag;
    }
    public ArrayList<Cloud_Tile> getClouds() {
        return this.clouds;
    }
    public ArrayList<Island_Tile> getIslands() {
        return this.islands;
    }
    public Boolean isExpertMode() {
        return this.expertMode;
    }
    public int getCoins() {
        if (this.expertMode) {
            return this.coins;
        } else {
            //System.out.println("Game is not in expert mode");
            return -1;
        }
    }
    public void setCoins(int coins) {
        this.coins=coins;
    }
    public ArrayList<CharacterCard> getCharacterCards() {
        if (this.expertMode) {
            return this.characterCards;
        } else {
            //System.out.println("Game is not in expert mode");
            return null;
        }
    }
    public int getDiningRoomMaxCapacity()
    {
        return diningRoomMaxCapacity;
    }
}
