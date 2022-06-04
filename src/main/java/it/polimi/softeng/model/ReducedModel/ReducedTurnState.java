package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.controller.TurnManager;

import java.io.Serializable;

public class ReducedTurnState implements Serializable {
    private final TurnManager.TurnState currentPhase;
    private final String currentPlayer;
    private final int remainingMoves;
    public ReducedTurnState(TurnManager turnManager) {
        this.currentPhase=turnManager.getTurnState();
        this.currentPlayer=turnManager.getCurrentPlayer().getName();
        this.remainingMoves=turnManager.getRemainingMoves();
    }
    public TurnManager.TurnState getCurrentPhase() {
        return this.currentPhase;
    }
    public String getCurrentPlayer() {
        return this.currentPlayer;
    }
    public int getRemainingMoves() {
        return this.remainingMoves;
    }
}
