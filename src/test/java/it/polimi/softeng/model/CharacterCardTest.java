package it.polimi.softeng.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CharacterCardTest {
    @Test
    public void testCharacterCardConstructor() {
        String testID="test";
        Integer testCost=0;
        CharacterCard testCard=new CharacterCard(testID,testCost);
        Assert.assertEquals(testCard.getCardID(),testID);
        Assert.assertEquals(testCard.getCost(),testCost);
    }
    @Test
    public void testIncrementCost() {
        Integer testValue=0;
        CharacterCard testCard=new CharacterCard("test",testValue);
        testCard.incrementCost();
        testValue+=1;
        assertEquals(testCard.getCost(),testValue);
    }
    @Test
    public void testGenCharacterCards() {
        Integer num=3;
        ArrayList<CharacterCard> testCards=CharacterCard.genCharacterCards(num);
        assertEquals("Character cards were not generated successfully (number generated differs from wanted)",num,Integer.valueOf(testCards.size()));
    }
}
