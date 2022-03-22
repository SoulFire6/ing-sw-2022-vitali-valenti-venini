package it.polimi.softeng.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

public class Game {
    private String gameID;
    private Integer playerNum;
    private ArrayList<Player> players;
    private EnumMap<Team,ArrayList<String>> teams;
    private Bag bag;
    private ArrayList<Cloud> clouds;
    private Boolean expertMode;
    private ArrayList<CharacterCard> characterCards;
    //TODO: decide if splitting for expertMode is better

    public Game(ArrayList<Player> players, Boolean expertMode) throws FileNotFoundException {
        Random rand=new Random();
        this.gameID=(Integer.valueOf(rand.nextInt())).toString();
        this.playerNum=players.size();
        this.players=players;
        this.teams=Team.genTeams(players,playerNum);
        this.bag=new Bag(26);
        this.clouds=Cloud.genClouds(this.playerNum);
        this.expertMode=expertMode;
        this.characterCards=CharacterCard.genCharacterCards();
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
    public Boolean getExpertMode() {
        return this.expertMode;
    }
    public ArrayList<CharacterCard> getCharacterCards() {
        if (expertMode) {
            return this.characterCards;
        } else {
            System.out.println("Game is not in expert mode");
            return null;
        }
    }
}
