package it.polimi.softeng.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssistantCardTest {
    @Test
    public void testAssistantCardConstructor() {
        String testID="test";
        int testTurn=3;
        int testMN=5;
        AssistantCard testCard=new AssistantCard(testID,testTurn,testMN);
        assertEquals(testCard.getCardID(),testID);
        assertEquals(testCard.getTurnValue(),testTurn);
        assertEquals(testCard.getMotherNatureValue(),testMN);
    }
}
