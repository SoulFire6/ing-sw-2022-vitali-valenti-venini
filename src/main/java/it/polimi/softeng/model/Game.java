package it.polimi.softeng.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

public class Game {
    private final String gameID;
    private final Integer playerNum;
    private final ArrayList<Player> players;
    //TODO: fix how teams are handled
    private final EnumMap<Team,ArrayList<String>> teams;
    private Bag_Tile bag;
    //Clouds are initialised from controller once
    private ArrayList<Cloud_Tile> clouds;
    private ArrayList<Island_Tile> islands;
    private Boolean expertMode;
    private ArrayList<CharacterCard> characterCards;

    public Game(String id, ArrayList<Player> players, Boolean expertMode) {
        this.gameID=id;
        this.playerNum=players.size();
        this.players=players;
        this.teams=Team.genTeams(players,playerNum);
        this.bag=new Bag_Tile(24);
        this.islands=Island_Tile.genIslands();
        this.expertMode=expertMode;
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
    public EnumMap<Team,ArrayList<String>> getTeams() {
        return this.teams;
    }
    public Boolean isExpertMode() {
        return this.expertMode;
    }
    public Bag_Tile getBag() {
        return this.bag;
    }
    public ArrayList<Cloud_Tile> getClouds() {
        return this.clouds;
    }
    public Boolean getExpertMode() {
        return this.expertMode;
    }
    public ArrayList<CharacterCard> getCharacterCards() {
        if (this.expertMode) {
            return this.characterCards;
        } else {
            System.out.println("Game is not in expert mode");
            return null;
        }
    }
}
