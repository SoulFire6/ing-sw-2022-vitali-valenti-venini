package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.PlayerNotFoundException;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedGame;

import java.util.ArrayList;
import java.util.Comparator;

public class TurnManager {
    private final ArrayList<Player> playerOrder;
    private Player currentPlayer=null;
    private final int maxMoves;
    private int remainingMoves;


    public enum TurnState {
        ASSISTANT_CARDS_PHASE("playing assistant cards"),
        MOVE_STUDENTS_PHASE("moving student diskss"),
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

    TurnState[] turnStates = TurnState.values();


    public TurnManager(ArrayList<Player> playerOrder, int maxMoves) {
        this.playerOrder = playerOrder;
        this.maxMoves=maxMoves;
        this.remainingMoves=maxMoves;
        this.currentPlayer = playerOrder.get(0);
        this.turnState = TurnState.ASSISTANT_CARDS_PHASE;
    }

    public TurnManager(ArrayList<Player> playerOrder, int maxMoves, ReducedGame save) {
        this.playerOrder=playerOrder;
        refreshTurnOrder();
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

    public void nextAction() {
        switch (turnState) {
            case ASSISTANT_CARDS_PHASE:
                if (currentPlayer==playerOrder.get(0)) {
                    for (Player player : playerOrder) {
                        player.getSchoolBoard().setLastUsedCard(null);
                    }
                }
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
