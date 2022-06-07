package it.polimi.softeng.model;

import java.util.ArrayList;
import java.util.EnumMap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CharacterCardTest {
    @Test
    public void testCharacterCardConstructor() {
        String testID="test";
        int testCost=0;
        CharID testCharID=CharID.MONK;
        CharacterCard testCard=new CharacterCard(testID,testCost,testCharID);
        assertEquals(testCard.getCardID(),testID);
        assertEquals(testCard.getCost(),testCost);
        assertEquals(testCard.getCharacter(),testCharID);
    }
    @Test
    public void testIncrementCost() {
        int testValue=0;
        CharacterCard testCard=new CharacterCard("test",testValue,null);
        testCard.incrementCost();
        testValue+=1;
        assertEquals(testCard.getCost(),testValue);
    }
}
