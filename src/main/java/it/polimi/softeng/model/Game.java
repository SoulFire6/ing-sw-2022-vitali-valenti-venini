package it.polimi.softeng.model;

import it.polimi.softeng.controller.TileController;
import it.polimi.softeng.controller.PlayerController;

import java.util.ArrayList;

public class Game {
    private final String gameID;
    private final ArrayList<Player> players;
    private final ArrayList<Team> teams;
    private final Bag_Tile bag;
    private final ArrayList<Cloud_Tile> clouds;
    private final ArrayList<Island_Tile> islands;
    private final boolean expertMode;
    private int coins;
    private final ArrayList<CharacterCard> characterCards;

    public Game(String gameID, ArrayList<Player> players, ArrayList<Team> teams, Bag_Tile bag, ArrayList<Cloud_Tile> clouds, ArrayList<Island_Tile> islands, boolean expertMode, int coins, ArrayList<CharacterCard> characterCards) {
        this.gameID=gameID;
        this.players=players;
        this.teams=teams;
        this.bag=bag;
        this.clouds=clouds;
        this.islands=islands;
        this.expertMode=expertMode;
        this.coins=coins;
        this.characterCards=characterCards;
    }
    public String getGameID() {
        return this.gameID;
    }
    public int getPlayerNum() {
        return this.players.size();
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
        return this.coins;
    }
    public void setCoins(int coins) {
        this.coins=coins;
    }
    public ArrayList<CharacterCard> getCharacterCards() {
        return this.characterCards;
    }
}
