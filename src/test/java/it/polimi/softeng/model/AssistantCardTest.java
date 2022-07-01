package it.polimi.softeng.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit tests for AssistantCard
 */
public class AssistantCardTest {
    /**
     * Testing default constructor
     */
    @Test
    public void testAssistantCardConstructor() {
        String testID="test";
        int testTurn=3;
        int testMN=5;
        AssistantCard testCard=new AssistantCard(testID,testTurn,testMN);
        assertEquals(testCard.getCardID(), testID);
        assertEquals(testCard.getTurnValue(),testTurn);
        assertEquals(testCard.getMotherNatureValue(),testMN);
    }
}
