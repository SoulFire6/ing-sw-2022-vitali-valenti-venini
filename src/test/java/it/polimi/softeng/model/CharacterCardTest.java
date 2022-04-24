package it.polimi.softeng.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CharacterCardTest {
    @Test
    public void testCharacterCardConstructor() {
        String testID="test";
        int testCost=0;
        CharacterCard testCard=new CharacterCard(testID,testCost);
        Assert.assertEquals(testCard.getCardID(),testID);
        Assert.assertEquals(testCard.getCost(),testCost);
    }
    @Test
    public void testIncrementCost() {
        int testValue=0;
        CharacterCard testCard=new CharacterCard("test",testValue);
        testCard.incrementCost();
        testValue+=1;
        assertEquals(testCard.getCost(),testValue);
    }
    @Test
    public void testMemory() {
        CharacterCard testCardNoMem=new CharacterCard("test_noMem",0);
        assertNull(testCardNoMem.getMemory());
        Object obj=new Object();
        //Testing example anonymous class, like the ones in controller
        CharacterCard testCardMem=new CharacterCard("test_mem",0,obj) {
            @Override
            public Object getMemory() {
                return this.memory;
            }
        };
        assertEquals(testCardMem.getMemory(),obj);
        Integer testInt=0;
        testCardMem.setMemory(testInt);
        assertEquals(testCardMem.getMemory(),testInt);
    }
}
