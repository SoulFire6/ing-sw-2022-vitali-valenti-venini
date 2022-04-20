package it.polimi.softeng.controller;

import it.polimi.softeng.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TurnManager {
    private ArrayList<Player> playerOrder;
    private Player currentPlayer;

    enum TurnState {
        STUDENTS_DRAW_PHASE,
        ASSISTANT_CARDS_PHASE,
        MOVE_STUDENTS_PHASE,
        MOVE_MOTHER_NATURE_PHASE,
        CHOOSE_CLOUD_TILE_PHASE
    }

    TurnState turnState;


    public TurnManager(ArrayList<Player> playerOrder)
    {
        this.playerOrder = playerOrder;
        currentPlayer = playerOrder.get(0);
        turnState = TurnState.STUDENTS_DRAW_PHASE;
    }

    public void nextPlayer()
    {
        if(currentPlayer.equals(playerOrder.get(playerOrder.size()-1)))    //currentPlayer is the last player of the list
            if(turnState==TurnState.CHOOSE_CLOUD_TILE_PHASE)        //last phase of the Turn
                refreshTurnOrder();
            else {                                                          //last player of playerOrder ended turn not in the last phase
                currentPlayer = playerOrder.get(0);                         //currentPlayer becomes the first of the List
                setNextAction();
            }
        else                                                                //currentPlayer is not the last one
            currentPlayer = playerOrder.get(playerOrder.indexOf(currentPlayer)+1);
    }

    public void refreshTurnOrder()          //This method is called after the whole round is finished
    {
            //Order the list based on the last played card by each Player
            playerOrder.sort((p1, p2) -> Integer.compare(p2.getSchoolBoard().getLastUsedCard().getTurnValue(), p1.getSchoolBoard().getLastUsedCard().getTurnValue()));

        currentPlayer=playerOrder.get(0);
        turnState = TurnState.STUDENTS_DRAW_PHASE;
    }


    public void setNextAction()   //Set turnState to the next state (the order is defined by its order in the enum declaration)
    {
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

}
