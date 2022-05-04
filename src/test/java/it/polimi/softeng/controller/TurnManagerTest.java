package it.polimi.softeng.controller;

import it.polimi.softeng.controller.Exceptions.AssistantCardNotFoundException;
import it.polimi.softeng.controller.Exceptions.AssistantCardTurnValueException;
import it.polimi.softeng.controller.Exceptions.MoveNotAllowedException;
import it.polimi.softeng.controller.Exceptions.NotYourTurnException;
import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TurnManagerTest {

    Controller controller, expertController;
    TurnManager turnManager;
    ArrayList<AssistantCard> cards;
    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        int testPlayerNum=2;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            playerNames.add("Player_"+i+1);
        }
        controller = new Controller(playerNames,false);
        expertController = new Controller(playerNames,true);
        turnManager = controller.turnManager;
    }

    @Test
    void nextAction() throws AssistantCardNotFoundException, AssistantCardTurnValueException, NotYourTurnException, MoveNotAllowedException {
        Player firstPlayer = turnManager.getCurrentPlayer();
        Player secondPlayer = turnManager.getPlayerOrder().get(1);
        //Planning phase Tests
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.STUDENTS_DRAW_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.ASSISTANT_CARDS_PHASE);
        controller.assistantCardController.playAssistantCard(firstPlayer,firstPlayer.getSchoolBoard().getHand().get(2),turnManager);
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.STUDENTS_DRAW_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.ASSISTANT_CARDS_PHASE);
        controller.assistantCardController.playAssistantCard(secondPlayer,secondPlayer.getSchoolBoard().getHand().get(0),turnManager);
        //Action phase Tests
        //Check if the turn is actually swapped (secondPlayer played a lower assistant turnvalue card
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.MOVE_STUDENTS_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.MOVE_STUDENTS_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE);
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);                                     //New Round
        assertEquals(turnManager.getTurnState(), TurnManager.TurnState.STUDENTS_DRAW_PHASE);
    }

    @Test
    void getNextPlayer() {
        Player currentPlayer = turnManager.getCurrentPlayer();
        assertEquals(turnManager.getNextPlayer(),turnManager.getPlayerOrder().get((turnManager.getPlayerOrder().indexOf(currentPlayer)+1)%turnManager.getPlayerOrder().size()));
    }

    @Test
    void refreshTurnOrder() throws AssistantCardNotFoundException, AssistantCardTurnValueException, NotYourTurnException, MoveNotAllowedException {
        turnManager.setNextAction();                                //to be able to play cards
        Player firstPlayer = turnManager.getCurrentPlayer();
        Player secondPlayer = turnManager.getPlayerOrder().get(1);
        controller.assistantCardController.playAssistantCard(firstPlayer,firstPlayer.getSchoolBoard().getHand().get(2),turnManager);
        turnManager.setNextAction();                                //to be able to play cards
        controller.assistantCardController.playAssistantCard(secondPlayer,secondPlayer.getSchoolBoard().getHand().get(0),turnManager);

        assertEquals(secondPlayer,turnManager.getCurrentPlayer());
        assertEquals(firstPlayer,turnManager.getNextPlayer());

    }

    @Test
    void setNextAction() {
        for(int i=0;i< TurnManager.TurnState.values().length;i++) {
            int nextIndex = turnManager.turnState.ordinal() + 1;
            TurnManager.TurnState[] turnStates = TurnManager.TurnState.values();
            nextIndex %= turnStates.length;
            turnManager.turnState = turnStates[nextIndex];

            assertEquals((i+1)% TurnManager.TurnState.values().length,turnManager.turnState.ordinal());
        }
    }


    @Test
    void getCurrentPlayer() {
        Player firstPlayer = turnManager.getPlayerOrder().get(0);
        assertEquals(firstPlayer,turnManager.getCurrentPlayer());
    }

    @Test
    void getTurnState() {
        assertEquals(controller.turnManager.getTurnState(),turnManager.turnState);
    }

    @Test
    void getPlayerOrder() {
        //Basic getter, no test
    }
}