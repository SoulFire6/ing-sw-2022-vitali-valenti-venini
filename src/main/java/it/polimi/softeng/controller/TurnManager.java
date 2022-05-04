package it.polimi.softeng.controller;

import it.polimi.softeng.model.Player;

import java.util.ArrayList;
import java.util.Comparator;

public class TurnManager {
    private final ArrayList<Player> playerOrder;
    private Player currentPlayer;


    public enum TurnState {
        STUDENTS_DRAW_PHASE,
        ASSISTANT_CARDS_PHASE,
        MOVE_STUDENTS_PHASE,
        MOVE_MOTHER_NATURE_PHASE,
        CHOOSE_CLOUD_TILE_PHASE
    }

    TurnState turnState;


    public TurnManager(ArrayList<Player> playerOrder) {
        this.playerOrder = playerOrder;
        currentPlayer = playerOrder.get(0);
        turnState = TurnState.STUDENTS_DRAW_PHASE;
    }

    public void nextAction() {
        //last phase of the Turn
        if(turnState==TurnState.CHOOSE_CLOUD_TILE_PHASE)
            //currentPlayer is the last player of the list
            if (currentPlayer.equals(playerOrder.get(playerOrder.size() - 1)))    {
                currentPlayer = getNextPlayer();
                turnState = TurnState.STUDENTS_DRAW_PHASE;
            } else {                                                          //last player of playerOrder ended turn not in the last phase
                currentPlayer = getNextPlayer();                        //currentPlayer becomes the first of the List
                turnState = TurnState.MOVE_STUDENTS_PHASE;
            }
        else if (turnState == TurnState.ASSISTANT_CARDS_PHASE) {
                if (currentPlayer.equals(playerOrder.get(playerOrder.size() - 1)))
                    refreshTurnOrder();
                else {
                    currentPlayer = getNextPlayer();
                    turnState = TurnState.STUDENTS_DRAW_PHASE;
                }
            }
        else
            setNextAction();
    }

    public Player getNextPlayer() {
        int currentIndex = playerOrder.indexOf(currentPlayer);
        return (playerOrder.get((currentIndex+1)% playerOrder.size()));     //return the next player
    }
    //This method is called after the whole round is finished
    public void refreshTurnOrder() {
        //Order the list based on the last played card by each Player
        playerOrder.sort(Comparator.comparingInt(p -> p.getSchoolBoard().getLastUsedCard().getTurnValue()));
        currentPlayer=playerOrder.get(0);
        turnState = TurnState.MOVE_STUDENTS_PHASE;
    }

    //Set turnState to the next state (the order is defined by its order in the enum declaration)
    public void setNextAction() {
        int nextIndex = turnState.ordinal()+1;
        TurnState[] turnStates = TurnState.values();
        nextIndex %= turnStates.length;
        turnState = turnStates[nextIndex];
    }

    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    public TurnState getTurnState()
    {
        return turnState;
    }

    public ArrayList<Player> getPlayerOrder()
    {
        return playerOrder;
    }

}
