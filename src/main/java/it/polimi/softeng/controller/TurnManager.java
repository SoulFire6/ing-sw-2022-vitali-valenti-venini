package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.InsufficientResourceException;
import it.polimi.softeng.exceptions.TileNotFoundException;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedGame;

import java.util.ArrayList;
import java.util.Comparator;


/**
 Controller class that handles players turn orders
 */

public class TurnManager {
    private final ArrayList<Player> playerOrder;
    private Player currentPlayer=null;
    private final int maxMoves;
    private int remainingMoves;


    public enum TurnState {
        ASSISTANT_CARDS_PHASE("playing assistant cards"),
        MOVE_STUDENTS_PHASE("moving student disks"),
        MOVE_MOTHER_NATURE_PHASE("moving mother nature"),
        CHOOSE_CLOUD_TILE_PHASE("choosing cloud");

        private final String description;
        TurnState(String description) {
            this.description=description;
        }
        public String getDescription() {
            return this.description;
        }
    }

    TurnState turnState;

    /**
     * This method is TurnManager constructor without loading from saved file
     * @param playerOrder list of all the players of the game
     * @param maxMoves the maximum number of moves that's allowed in a turn
     */

    public TurnManager(ArrayList<Player> playerOrder, int maxMoves) {
        this.playerOrder = playerOrder;
        this.maxMoves=maxMoves;
        this.remainingMoves=maxMoves;
        this.currentPlayer = playerOrder.get(0);
        this.turnState = TurnState.ASSISTANT_CARDS_PHASE;
    }

    TurnState[] turnStates = TurnState.values();

    /**
     * This method is TurnManager constructor loading from saved file
     * @param playerOrder list of all the players of the game
     * @param maxMoves the maximum number of moves that's allowed in a turn
     * @param save contains the state of the game saved
     */
    public TurnManager(ArrayList<Player> playerOrder, int maxMoves, ReducedGame save) {
        this.playerOrder=playerOrder;
        this.maxMoves=maxMoves;
        this.remainingMoves=save.getRemainingMoves();
        for (Player p : playerOrder) {
            if (p.getName().equals(save.getCurrentPlayer())) {
                this.currentPlayer=p;
                break;
            }
        }
        this.turnState=save.getCurrentPhase();
    }

    /**
     * This method is responsible for changing the turnState attribute based on the current player and the previous turnState value
     */

    public void nextAction() {
        switch (turnState) {
            case ASSISTANT_CARDS_PHASE:
                //if there are still players left that need to play switch player and refresh remaining moves
                if (currentPlayer!=getLastPlayer()) {
                    currentPlayer=getNextPlayer();
                } else {
                    refreshTurnOrder();
                    turnState=TurnState.MOVE_STUDENTS_PHASE;
                    currentPlayer=playerOrder.get(0);
                }
                break;
            case CHOOSE_CLOUD_TILE_PHASE:
                //if last player has finished his action phase return to planning phase, otherwise start next player's action phase
                if (currentPlayer==getLastPlayer()) {
                    //resets last played card for new round
                    for (Player player : playerOrder) {
                        player.getSchoolBoard().setLastUsedCard(null);
                    }
                    turnState=TurnState.ASSISTANT_CARDS_PHASE;
                    currentPlayer=playerOrder.get(0);
                } else {
                    turnState=TurnState.MOVE_STUDENTS_PHASE;
                    remainingMoves=maxMoves;
                    currentPlayer=getNextPlayer();
                }
                break;
            case MOVE_STUDENTS_PHASE:
                if (remainingMoves==1) {
                    remainingMoves=maxMoves;
                    turnState=turnStates[(turnState.ordinal()+1)%turnStates.length];
                } else {
                    remainingMoves--;
                }
                break;
            default:
                turnState=turnStates[(turnState.ordinal()+1)%turnStates.length];
                break;
        }
    }

    /**
     * This method is called after whole round is finished, and sort the playerOrder list for the next round
     */
    //This method is called after the whole round is finished
    public void refreshTurnOrder() {
        //Order the list based on the last played card by each Player
        playerOrder.sort(Comparator.comparingInt(p -> p.getSchoolBoard().getLastUsedCard()==null?11:p.getSchoolBoard().getLastUsedCard().getTurnValue()));
        currentPlayer=playerOrder.get(0);
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
    public Player getNextPlayer() {
        return this.playerOrder.get((playerOrder.indexOf(currentPlayer)+1)%playerOrder.size());
    }
    public Player getLastPlayer() {
        return this.playerOrder.get(playerOrder.size()-1);
    }
    public TurnState getTurnState() {
        return this.turnState;
    }
    public int getMaxMoves() {
        return this.maxMoves;
    }
    public int getRemainingMoves() {
        return this.remainingMoves;
    }
    public ArrayList<Player> getPlayerOrder() {
        return this.playerOrder;
    }

}
