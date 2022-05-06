package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.*;

import java.io.Serializable;
import java.util.ArrayList;

public class ReducedGame implements Serializable {
    private final String id;
    private ArrayList<ReducedPlayer> players;
    private ReducedBag bag;
    private ArrayList<ReducedCloud> clouds;
    private ArrayList<ReducedIsland> islands;
    private final boolean expertMode;
    private int coins;
    private ArrayList<ReducedCharacterCard> characterCards;

    public ReducedGame(Game game) {
        this.id=game.getGameID();
        this.players=setupPlayers(game.getPlayers());
        this.bag=new ReducedBag(game.getBag());
        this.clouds=setupClouds(game.getClouds());
        this.islands=setupIslands(game.getIslands());
        this.expertMode=game.isExpertMode();
        this.coins=game.getCoins();
        this.characterCards=setupCharacterCards(game.getCharacterCards());
    }
    private ArrayList<ReducedPlayer> setupPlayers(ArrayList<Player> gamePlayers) {
        ArrayList<ReducedPlayer> reducedPlayers=new ArrayList<>();
        for (Player p : gamePlayers) {
            reducedPlayers.add(new ReducedPlayer(p));
        }
        return reducedPlayers;
    }
    private ArrayList<ReducedCloud> setupClouds(ArrayList<Cloud_Tile> gameClouds) {
        ArrayList<ReducedCloud> reducedClouds=new ArrayList<>();
        for (Cloud_Tile cloud : gameClouds) {
            reducedClouds.add(new ReducedCloud(cloud));
        }
        return reducedClouds;
    }
    private ArrayList<ReducedIsland> setupIslands(ArrayList<Island_Tile> gameIslands) {
        ArrayList<ReducedIsland> reducedIslands=new ArrayList<>();
        for (Island_Tile island : gameIslands) {
            reducedIslands.add(new ReducedIsland(island));
        }
        return reducedIslands;
    }
    private ArrayList<ReducedCharacterCard> setupCharacterCards(ArrayList<CharacterCard> gameCards) {
        ArrayList<ReducedCharacterCard> reducedCharCards=new ArrayList<>();
        for (CharacterCard card : gameCards) {
            reducedCharCards.add(new ReducedCharacterCard(card));
        }
        return reducedCharCards;
    }

    public String getId() {
        return this.id;
    }
    public ArrayList<ReducedPlayer> getPlayers() {
        return this.players;
    }
    public void setPlayers(ArrayList<ReducedPlayer> players) {
        this.players=players;
    }
    public ReducedBag getBag() {
        return this.bag;
    }
    public void setBag(ReducedBag bag) {
        this.bag=bag;
    }
    public ArrayList<ReducedCloud> getClouds() {
        return clouds;
    }
    public void setClouds(ArrayList<ReducedCloud> clouds) {
        this.clouds=clouds;
    }
    public ArrayList<ReducedIsland> getIslands() {
        return islands;
    }
    public void setIslands(ArrayList<ReducedIsland> islands) {
        this.islands=islands;
    }
    public boolean isExpertMode() {
        return expertMode;
    }
    public void setCoins(int coins) {
        this.coins=coins;
    }
    public Integer getCoins() {
        return coins;
    }
    public ArrayList<ReducedCharacterCard> getCharacterCards() {
        return characterCards;
    }
    public void setCharacterCards(ArrayList<ReducedCharacterCard> characterCards) {
        this.characterCards = characterCards;
    }
}
