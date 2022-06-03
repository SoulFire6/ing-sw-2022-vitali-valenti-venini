package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TurnManagerTest {

    LobbyController controller, expertController;
    TurnManager turnManager;
    ArrayList<AssistantCard> cards;
    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        int testPlayerNum=2;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            playerNames.add("Player_"+(i+1));
        }
        try {
            controller = new LobbyController(playerNames,false,"normal lobby");
            expertController = new LobbyController(playerNames,true,"expert lobby");
            turnManager = controller.getTurnManager();
        }
        catch (InvalidPlayerNumException ipne) {
            fail();
        }
    }

    @Test
    void nextAction() throws AssistantCardNotFoundException, AssistantCardAlreadyPlayedException {
        Player firstPlayer = turnManager.getCurrentPlayer();
        Player secondPlayer = turnManager.getNextPlayer();
        //Planning phase Tests
        assertEquals(TurnManager.TurnState.ASSISTANT_CARDS_PHASE,turnManager.getTurnState());
        assertEquals(firstPlayer,turnManager.getCurrentPlayer());
        controller.getAssistantCardController().playAssistantCard(turnManager.getCurrentPlayer(),turnManager.getCurrentPlayer().getSchoolBoard().getHand().get(1).getCardID(),turnManager.getPlayerOrder());
        assertEquals(TurnManager.TurnState.ASSISTANT_CARDS_PHASE,turnManager.getTurnState());
        assertEquals(secondPlayer,turnManager.getCurrentPlayer());
        controller.getAssistantCardController().playAssistantCard(turnManager.getCurrentPlayer(),turnManager.getCurrentPlayer().getSchoolBoard().getHand().get(0).getCardID(),turnManager.getPlayerOrder());
        assertEquals(secondPlayer,turnManager.getCurrentPlayer());
        //Action phase Tests
        //Check if the turn is actually swapped (secondPlayer played a lower assistant turnvalue card
        assertEquals(secondPlayer,turnManager.getCurrentPlayer());
        assertEquals(TurnManager.TurnState.MOVE_STUDENTS_PHASE,turnManager.getTurnState());
        //skipping student disk moving
        while (turnManager.getTurnState()==TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
            turnManager.nextAction();
        }
        assertEquals(secondPlayer,turnManager.getCurrentPlayer());
        assertEquals(TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE,turnManager.getTurnState());
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);
        assertEquals(TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE,turnManager.getTurnState());
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(TurnManager.TurnState.MOVE_STUDENTS_PHASE,turnManager.getTurnState());
        while (turnManager.getTurnState()==TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
            turnManager.nextAction();
        }
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE,turnManager.getTurnState());
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), firstPlayer);
        assertEquals(TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE,turnManager.getTurnState());
        turnManager.nextAction();
        assertEquals(turnManager.getCurrentPlayer(), secondPlayer);                                     //New Round
        assertEquals(TurnManager.TurnState.ASSISTANT_CARDS_PHASE,turnManager.getTurnState());
    }

    @Test
    void refreshTurnOrder() throws AssistantCardNotFoundException, AssistantCardAlreadyPlayedException {
        Player firstPlayer = turnManager.getCurrentPlayer();
        Player secondPlayer = turnManager.getPlayerOrder().get(1);
        controller.getAssistantCardController().playAssistantCard(firstPlayer,firstPlayer.getSchoolBoard().getHand().get(2).getCardID(),turnManager.getPlayerOrder());
        controller.getAssistantCardController().playAssistantCard(secondPlayer,secondPlayer.getSchoolBoard().getHand().get(0).getCardID(),turnManager.getPlayerOrder());
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
        assertEquals(controller.getTurnManager().getTurnState(),turnManager.turnState);
    }

    @Test
    void testGetMoves() {
        int testMaxMoves=controller.getGame().getClouds().get(0).getMaxSlots();
        for (Player p: controller.getGame().getPlayers()) {
            p.getSchoolBoard().playAssistantCard("Cheetah");
        }
        while(turnManager.getTurnState()!= TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
            turnManager.nextAction();
        }
        assertEquals(testMaxMoves,turnManager.getMaxMoves());
        turnManager.nextAction();
        assertEquals(testMaxMoves-1,turnManager.getRemainingMoves());
    }
}