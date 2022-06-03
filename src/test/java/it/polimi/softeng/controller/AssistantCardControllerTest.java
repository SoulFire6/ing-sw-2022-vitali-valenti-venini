package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.AssistantCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AssistantCardControllerTest {
    AssistantCardController assistantCardController=new AssistantCardController();
    LobbyController controller;
    ArrayList<AssistantCard> cards = new ArrayList<>();
    @BeforeEach
    void setUp() {
        int testPlayerNum=2;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            playerNames.add("Player_"+(i+1));
        }
        try {
            controller = new LobbyController(playerNames,false,"normal lobby");
        }
        catch (InvalidPlayerNumException ipne) {
            fail();
        }
    }

    @Test
    void genHand() {
        AssistantCardController assistantCardController=new AssistantCardController();
        cards = assistantCardController.genHand();
        assertEquals(10, cards.size());
        for (AssistantCard card : cards) {
            assertNotNull(card);
        }
    }

    @Test
    void playAssistantCard() {
        TurnManager turnManager=controller.getTurnManager();
        cards = turnManager.getCurrentPlayer().getSchoolBoard().getHand();
        assertDoesNotThrow(()->assistantCardController.playAssistantCard(turnManager.getCurrentPlayer(),cards.get(0).getCardID(),turnManager.getPlayerOrder()));
        turnManager.nextAction();
        assertEquals(9,turnManager.getPlayerOrder().get(0).getSchoolBoard().getHand().size());           //Verify that after getting played the card got removed
        cards = turnManager.getCurrentPlayer().getSchoolBoard().getHand();
        assertThrows(AssistantCardAlreadyPlayedException.class,()->assistantCardController.playAssistantCard(turnManager.getCurrentPlayer(),cards.get(0).getCardID(),turnManager.getPlayerOrder()));//Trying to play card with same value of the card played by the other player in the same turn
        assertDoesNotThrow(()->assistantCardController.playAssistantCard(turnManager.getCurrentPlayer(),cards.get(1).getCardID(),turnManager.getPlayerOrder()));
        assertThrows(AssistantCardNotFoundException.class,()->assistantCardController.playAssistantCard(turnManager.getCurrentPlayer(),"illegal id",turnManager.getPlayerOrder()));
    }
}