package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.model.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is part of the reduced model and represents its game
 */
public class ReducedGame implements Serializable {

    /**
     * Enumeration class used to know which type of update is necessary
     */
    public enum UpdateType{
        PLAYERS,PLAYER,BAG,CLOUDS,ISLANDS,COINS,CHARACTER_CARDS,TURN_STATE,LOADED_GAME
    }
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

    /**
     * This is the constructor. It initializes a reduced version of a Game.
     * @param game the game that will get reduced
     * @param turnManager the TurnManager of the game
     */
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

    /**
     * Method that transforms an ArrayList<Player> into an ArrayList<ReducedPlayer>
     * @param gamePlayers ArrayList<Player> containing the players we want to get reduced
     * @return ArrayList<ReducedPlayer> list of the reduced players
     */
    private ArrayList<ReducedPlayer> setupPlayers(ArrayList<Player> gamePlayers) {
        ArrayList<ReducedPlayer> reducedPlayers=new ArrayList<>();
        for (Player p : gamePlayers) {
            reducedPlayers.add(new ReducedPlayer(p));
        }
        return reducedPlayers;
    }
    /**
     * Method that transforms an ArrayList<Cloud_Tile> into an ArrayList<ReducedCloud>
     * @param gameClouds ArrayList<Cloud_Tile> containing the cloud tiles we want to get reduced
     * @return ArrayList<ReducedCloud> list of the reduced cloud tiles
     */
    private ArrayList<ReducedCloud> setupClouds(ArrayList<Cloud_Tile> gameClouds) {
        ArrayList<ReducedCloud> reducedClouds=new ArrayList<>();
        for (Cloud_Tile cloud : gameClouds) {
            reducedClouds.add(new ReducedCloud(cloud));
        }
        return reducedClouds;
    }

    /**
     * Method that transforms an ArrayList<Island_Tile> into an ArrayList<ReducedIsland>
     * @param gameIslands ArrayList<Island_Tile> containing the island tiles we want to get reduced
     * @return ArrayList<ReducedIsland> list of the reduced islands
     */
    private ArrayList<ReducedIsland> setupIslands(ArrayList<Island_Tile> gameIslands) {
        ArrayList<ReducedIsland> reducedIslands=new ArrayList<>();
        for (Island_Tile island : gameIslands) {
            reducedIslands.add(new ReducedIsland(island));
        }
        return reducedIslands;
    }

    /**
     * Method that transforms an ArrayList<CharacterCard> into an ArrayList<ReducedCharacterCard>
     * @param gameCards ArrayList<CharacterCard> containing the character cards we want to get reduced
     * @return ArrayList<ReducedCharacterCard> list of the reduced character cards
     */

    private ArrayList<ReducedCharacterCard> setupCharacterCards(ArrayList<CharacterCard> gameCards) {
        ArrayList<ReducedCharacterCard> reducedCharCards=new ArrayList<>();
        for (CharacterCard card : gameCards) {
            reducedCharCards.add(new ReducedCharacterCard(card));
        }
        return reducedCharCards;
    }

    /**
     * @return String id of this reduced game
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return ArrayList<ReducedPlayer> list of reduced players of this reduced game
     */
    public ArrayList<ReducedPlayer> getPlayers() {
        return this.players;
    }

    /**
     * This method is used to set the players of this reduced game
     * @param players ArrayList<ReducedPlayer> containing the reduced players we want to set
     */
    public void setPlayers(ArrayList<ReducedPlayer> players) {
        this.players=players;
        propertyChangeSupport.firePropertyChange(UpdateType.PLAYERS.name(),null,this.players);
    }

    /**
     * This method is used to set a single player of this reduced game
     * @param player the reduced player we want to set
     */
    public void setPlayer(ReducedPlayer player) {
        for (ReducedPlayer reducedPlayer : this.players) {
            if (reducedPlayer.getName().equals(player.getName())) {
                this.players.set(this.players.indexOf(reducedPlayer),player);
                propertyChangeSupport.firePropertyChange(UpdateType.PLAYER.name(), null,reducedPlayer);
                break;
            }
        }
    }

    /**
     * @return ReducedBag the bag of this reduced game
     */
    public ReducedBag getBag() {
        return this.bag;
    }

    /**
     * @param bag ReducedBag we want to set in this reduced game
     */
    public void setBag(ReducedBag bag) {
        this.bag=bag;
        propertyChangeSupport.firePropertyChange(UpdateType.BAG.name(), null,this.bag);
    }

    /**
     * @return ArrayList<ReducedCloud> the clouds of this reduced game
     */
    public ArrayList<ReducedCloud> getClouds() {
        return clouds;
    }

    /**
     * @param clouds ArrayList<ReducedCloud> we want to set in this reduced game
     */
    public void setClouds(ArrayList<ReducedCloud> clouds) {
        this.clouds=clouds;
        propertyChangeSupport.firePropertyChange(UpdateType.CLOUDS.name(), null,this.clouds);
    }

    /**
     * @return ArrayList<ReducedIsland> containing the islands of this reduced game
     */
    public ArrayList<ReducedIsland> getIslands() {
        return islands;
    }

    /**
     * @param islands ArrayList<ReducedIsland> we want to set in this reduced game
     */
    public void setIslands(ArrayList<ReducedIsland> islands) {
        this.islands=islands;
        propertyChangeSupport.firePropertyChange(UpdateType.ISLANDS.name(), null,this.islands);
    }

    /**
     * @return true if the game is in expert mode, false otherwise
     */
    public boolean isExpertMode() {
        return expertMode;
    }

    /**
     * @return Integer the coins available in the current state of the game
     */
    public Integer getCoins() {
        return coins;
    }

    /**
     * @param coins int number of the reduced game coins we want to set
     */
    public void setCoins(int coins) {
        this.coins=coins;
        this.propertyChangeSupport.firePropertyChange(UpdateType.COINS.name(), null,this.coins);
    }

    /**
     * @return ArrayList<ReducedCharacterCard> containing the character cards of the reduced game
     */
    public ArrayList<ReducedCharacterCard> getCharacterCards() {
        return this.characterCards;
    }

    /**
     * @param characterCards ArrayList<ReducedCharacterCard> we want to set in this reduced game
     */
    public void setCharacterCards(ArrayList<ReducedCharacterCard> characterCards) {
        this.characterCards = characterCards;
        this.propertyChangeSupport.firePropertyChange(UpdateType.CHARACTER_CARDS.name(), null,this.characterCards);
    }

    /**
     * @param turnState ReducedTurnState we want to set in this reduced game's state
     */
    public void setTurnState(ReducedTurnState turnState) {
        this.currentPlayer=turnState.getCurrentPlayer();
        this.currentPhase=turnState.getCurrentPhase();
        this.remainingMoves= turnState.getRemainingMoves();
        this.propertyChangeSupport.firePropertyChange(UpdateType.TURN_STATE.name(), null,this);
    }

    /**
     * @return String name of the current player
     */
    public String getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * @return the state of the current phase of the game
     */
    public TurnManager.TurnState getCurrentPhase() {
        return this.currentPhase;
    }

    /**
     * @return int number of remaining moves in this state of the game
     */
    public int getRemainingMoves() {
        return this.remainingMoves;
    }

    /**
     * This method adds a listener to the model
     * @param listener the listener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    /**
     * This method removes a listener from the model
     * @param listener the listener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * This method notifies the view the first time the model is loaded on the client
     */
    public void notifyGameLoaded() {
        this.propertyChangeSupport.firePropertyChange(UpdateType.LOADED_GAME.name(), null,this);
    }
}
