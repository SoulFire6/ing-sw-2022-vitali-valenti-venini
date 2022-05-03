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

class AssistantCardControllerTest {
    AssistantCardController assistantCardController;
    Controller controller, expertController;
    ArrayList<AssistantCard> cards;
    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        assistantCardController=new AssistantCardController();

        int testPlayerNum=2;
        ArrayList<String> playerNames=new ArrayList<>();
        for (int i=0; i<testPlayerNum; i++) {
            playerNames.add("Player_"+i+1);
        }
        controller = new Controller(playerNames,false);
        expertController = new Controller(playerNames,true);
    }

    @Test
    void genHand() {
        cards = AssistantCardController.genHand(null);
        assertEquals(10, cards.size());
        assertTrue(cards.contains(cards.get(0)));
    }

    @Test
    void playAssistantCard() throws AssistantCardNotFoundException, AssistantCardTurnValueException, NotYourTurnException, MoveNotAllowedException {
        cards = controller.turnManager.getCurrentPlayer().getSchoolBoard().getHand();
        assertThrows(NotYourTurnException.class, ()->assistantCardController.playAssistantCard(controller.turnManager.getNextPlayer(), cards.get(0),controller.turnManager ));          //Trying to play a card in another player's turn
        assertThrows(MoveNotAllowedException.class, ()->assistantCardController.playAssistantCard(controller.turnManager.getCurrentPlayer(),cards.get(0),controller.turnManager ));     //Trying to play a card in a not allowed phase
        controller.turnManager.setNextAction();
        assertDoesNotThrow(()->assistantCardController.playAssistantCard(controller.turnManager.getCurrentPlayer(),cards.get(0),controller.turnManager ));
        assertEquals(controller.turnManager.getPlayerOrder().get(0).getSchoolBoard().getHand().size(),9);           //Verify that after getting played the card got removed
        assertThrows(MoveNotAllowedException.class,()->assistantCardController.playAssistantCard(controller.turnManager.getCurrentPlayer(),cards.get(0),controller.turnManager ));       //Trying to play an already removed assistantcard
        controller.turnManager.setNextAction();
        cards = controller.turnManager.getCurrentPlayer().getSchoolBoard().getHand();
        assertThrows(AssistantCardTurnValueException.class,()->assistantCardController.playAssistantCard(controller.turnManager.getCurrentPlayer(),cards.get(0),controller.turnManager ));//Trying to play card with same value of the card played by the other player in the same turn
        assertDoesNotThrow(()->assistantCardController.playAssistantCard(controller.turnManager.getCurrentPlayer(),cards.get(1),controller.turnManager ));
    }
}