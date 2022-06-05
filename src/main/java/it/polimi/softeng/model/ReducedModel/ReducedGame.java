package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.model.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
    private String currentPlayer;
    private TurnManager.TurnState currentPhase;
    private int remainingMoves;
    private final PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);

    public ReducedGame(Game game, TurnManager turnManager) {
        this.id=game.getGameID();
        this.players=setupPlayers(game.getPlayers());
        this.bag=new ReducedBag(game.getBag());
        this.clouds=setupClouds(game.getClouds());
        this.islands=setupIslands(game.getIslands());
        this.expertMode=game.isExpertMode();
        this.coins=game.getCoins();
        this.characterCards=this.expertMode?setupCharacterCards(game.getCharacterCards()):null;
        this.currentPlayer=turnManager.getCurrentPlayer().getName();
        this.currentPhase=turnManager.getTurnState();
        this.remainingMoves=turnManager.getRemainingMoves();
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
        propertyChangeSupport.firePropertyChange("players",null,this);
    }
    public void setPlayer(ReducedPlayer player) {
        for (ReducedPlayer reducedPlayer : this.players) {
            if (reducedPlayer.getName().equals(player.getName())) {
                this.players.set(this.players.indexOf(reducedPlayer),player);
                propertyChangeSupport.firePropertyChange("player",null,this);
                break;
            }
        }
    }
    public ReducedBag getBag() {
        return this.bag;
    }
    public void setBag(ReducedBag bag) {
        this.bag=bag;
        propertyChangeSupport.firePropertyChange("bag",null,this);
    }
    public ArrayList<ReducedCloud> getClouds() {
        return clouds;
    }
    public void setClouds(ArrayList<ReducedCloud> clouds) {
        this.clouds=clouds;
        propertyChangeSupport.firePropertyChange("clouds",null,this);
    }
    public ArrayList<ReducedIsland> getIslands() {
        return islands;
    }
    public void setIslands(ArrayList<ReducedIsland> islands) {
        this.islands=islands;
        propertyChangeSupport.firePropertyChange("islands",null,this);
    }
    public boolean isExpertMode() {
        return expertMode;
    }
    public Integer getCoins() {
        return coins;
    }
    public void setCoins(int coins) {
        this.coins=coins;
        this.propertyChangeSupport.firePropertyChange("coins",null,this);
    }
    public ArrayList<ReducedCharacterCard> getCharacterCards() {
        return this.characterCards;
    }
    public void setCharacterCards(ArrayList<ReducedCharacterCard> characterCards) {
        this.characterCards = characterCards;
        this.propertyChangeSupport.firePropertyChange("character cards",null,this);
    }

    public void setTurnState(ReducedTurnState turnState) {
        this.currentPlayer=turnState.getCurrentPlayer();
        this.currentPhase=turnState.getCurrentPhase();
        this.remainingMoves= turnState.getRemainingMoves();
        this.propertyChangeSupport.firePropertyChange("turn state",null,this);
    }

    public String getCurrentPlayer() {
        return this.currentPlayer;
    }

    public TurnManager.TurnState getCurrentPhase() {
        return this.currentPhase;
    }

    public int getRemainingMoves() {
        return this.remainingMoves;
    }

    //Adds a listener to model
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    //notifies view the first time the model is loaded on the client
    public void notifyGameLoaded() {
        this.propertyChangeSupport.firePropertyChange("Loaded game",null,this);
    }
}
