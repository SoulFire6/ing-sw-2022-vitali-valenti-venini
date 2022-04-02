package it.polimi.softeng.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AssistantCardTest {
    @Test
    public void testAssistantCardConstructor() {
        String testID="test";
        Integer testTurn=3;
        Integer testMN=5;
        AssistantCard testCard=new AssistantCard(testID,testTurn,testMN);
        assertEquals(testCard.getCardID(),testID);
        assertEquals(testCard.getTurnValue(),testTurn);
        assertEquals(testCard.getMotherNatureValue(),testMN);
    }
    @Test
    public void testGenHand() {
        ArrayList<AssistantCard> testCards=AssistantCard.genHand();
        assertEquals("Hand was not generated properly",10,testCards.size());
    }
}
