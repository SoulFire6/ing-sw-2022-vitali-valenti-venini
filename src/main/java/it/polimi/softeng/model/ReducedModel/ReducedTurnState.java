package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.controller.TurnManager;

import java.io.Serializable;

/**
 * This class is part of the reduced model and represents its turn states
 */
public class ReducedTurnState implements Serializable {
    private final TurnManager.TurnState currentPhase;
    private final String currentPlayer;
    private final int remainingMoves;

    /**
     * This is the constructor. It initializes the turn state of the reduced model.
     * @param turnManager object from which the state of the game is loaded
     */
    public ReducedTurnState(TurnManager turnManager) {
        this.currentPhase=turnManager.getTurnState();
        this.currentPlayer=turnManager.getCurrentPlayer().getName();
        this.remainingMoves=turnManager.getRemainingMoves();
    }

    /**
     * @return the current state of the turn
     */
    public TurnManager.TurnState getCurrentPhase() {
        return this.currentPhase;
    }

    /**
     * @return String current player's name
     */
    public String getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * @return int number of remaining moves for the current state of the turn
     */
    public int getRemainingMoves() {
        return this.remainingMoves;
    }
}
