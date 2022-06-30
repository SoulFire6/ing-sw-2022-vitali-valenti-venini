package it.polimi.softeng.model;

import java.util.ArrayList;

/**
 * This class it's part of the model and represents the Game.
 */

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

    /**
     * This is the class constructor, and it initializes the Game.
     * @param gameID String identifier of the game
     * @param players ArrayList<Player> containing the players of this game
     * @param teams ArrayList<Team> containing the teams of this game
     * @param bag the bag of the game
     * @param clouds the clouds of the game
     * @param islands the islands of the game
     * @param expertMode Boolean true if the game is in expert mode, false otherwise
     * @param coins int number of coins
     * @param characterCards the character cards that is possible using in this game
     */
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

    /**
     * @return String identifier of the game
     */
    public String getGameID() {
        return this.gameID;
    }

    /**
     * @return int number of this game's players
     */
    public int getPlayerNum() {
        return this.players.size();
    }

    /**
     * @return ArrayList<Player> containing the players of this game
     */
    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    /**
     * @return ArrayList<Team> containing the teams of this game
     */
    public ArrayList<Team> getTeams() {
        return this.teams;
    }

    /**
     * @return the Bag instance of this game
     */
    public Bag_Tile getBag() {
        return this.bag;
    }

    /**
     * @return ArrayList<Cloud_Tile> containing the Cloud_Tile instances of this game
     */
    public ArrayList<Cloud_Tile> getClouds() {
        return this.clouds;
    }

    /**
     * @return ArrayList<Island_Tile> containing the Island_Tile instances of this game
     */
    public ArrayList<Island_Tile> getIslands() {
        return this.islands;
    }

    /**
     * @return Boolean true if game is in expert mode, false otherwise
     */
    public Boolean isExpertMode() {
        return this.expertMode;
    }

    /**
     * @return int number of coins available in the game
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * @param coins int number of coins to be set
     */
    public void setCoins(int coins) {
        this.coins=coins;
    }

    /**
     * @return ArrayList<CharacterCard> containing the character cards of the current game
     */
    public ArrayList<CharacterCard> getCharacterCards() {
        return this.characterCards;
    }
}
