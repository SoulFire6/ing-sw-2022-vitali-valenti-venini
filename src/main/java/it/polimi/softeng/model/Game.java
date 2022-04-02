package it.polimi.softeng.model;

import java.util.ArrayList;

public class Game {
    private final String gameID;
    private final Integer playerNum;
    private final ArrayList<Player> players;
    private ArrayList<Team> teams;
    private Bag_Tile bag;
    private ArrayList<Cloud_Tile> clouds;
    private ArrayList<Island_Tile> islands;
    private boolean expertMode;
    private Integer coins;
    private ArrayList<CharacterCard> characterCards;

    //Normal game constructor
    public Game(String gameID, ArrayList<Player> players, Integer bagFill, Integer cloudNum, Integer cloudMax, Integer islandNum) {
        this.gameID=gameID;
        this.playerNum=players.size();
        this.players=players;
        this.teams=Team.getTeams(this.players);
        this.bag=new Bag_Tile(bagFill);
        this.clouds=Cloud_Tile.genClouds(cloudNum,cloudMax);
        this.islands=Island_Tile.genIslands(islandNum);
        this.expertMode=false;
    }

    //Expert game constructor
    public Game(String gameID, ArrayList<Player> players,Integer bagFill, Integer cloudNum, Integer cloudMax,Integer islandNum, Integer coins, Integer charCardNum) {
        this(gameID,players,bagFill,cloudNum,cloudMax,islandNum);
        this.expertMode=true;
        this.coins=coins;
        this.characterCards=CharacterCard.genCharacterCards(charCardNum);
    }
    public String getGameID() {
        return this.gameID;
    }
    public Integer getPlayerNum() {
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
    public Integer getCoins() {
        if (this.expertMode) {
            return this.coins;
        } else {
            //System.out.println("Game is not in expert mode");
            return null;
        }
    }
    public void setCoins(Integer coins) {
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
}
