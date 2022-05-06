package it.polimi.softeng.model;

import it.polimi.softeng.model.CharacterCardSubTypes.ColourBooleanMap_CharCard;
import it.polimi.softeng.model.CharacterCardSubTypes.ColourPlayerMap_CharCard;
import it.polimi.softeng.model.CharacterCardSubTypes.Int_CharCard;
import it.polimi.softeng.model.CharacterCardSubTypes.StudentDisk_CharCard;

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
        CharacterCard testCard=new CharacterCard(testID,testCost);
        assertEquals(testCard.getCardID(),testID);
        assertEquals(testCard.getCost(),testCost);
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
    public void testSubTypeMemory() {
        EnumMap<Colour,Integer> testIntegerMap=Colour.genIntegerMap();
        EnumMap<Colour,Boolean> testBooleanMap=Colour.genBooleanMap();
        EnumMap<Colour,Player> testPlayerMap=Colour.genPlayerMap();
        Integer testInteger=4;
        int testCost=3;
        String testId="charCardSubtype";
        Colour c=Colour.getRandomColour();
        Player p=new Player("test",null);
        ArrayList<CharacterCard> charCardSubtypes=new ArrayList<>();
        //Integer map memory check
        StudentDisk_CharCard testIntegerMapCard=new StudentDisk_CharCard(testId,testCost,testIntegerMap.clone());
        assertEquals(testIntegerMap,testIntegerMapCard.getMemory());
        testIntegerMap.put(c,testInteger);
        assertNotEquals(testIntegerMap,testIntegerMapCard.getMemory());
        testIntegerMapCard.setMemory(testIntegerMap);
        assertEquals(testIntegerMap,testIntegerMapCard.getMemory());
        assertEquals(testInteger,testIntegerMapCard.getMemory().get(c));
        charCardSubtypes.add(testIntegerMapCard);
        //Boolean map memory check
        ColourBooleanMap_CharCard testBooleanMapCard=new ColourBooleanMap_CharCard(testId,testCost,testBooleanMap);
        assertEquals(testBooleanMap,testBooleanMapCard.getMemory());
        testBooleanMap.put(c,true);
        assertNotEquals(testBooleanMap,testBooleanMapCard.getMemory());
        testBooleanMapCard.setMemory(testBooleanMap);
        assertEquals(testBooleanMap,testBooleanMapCard.getMemory());
        testBooleanMapCard.resetMemory();
        assertNotEquals(testBooleanMap,testBooleanMapCard.getMemory());
        assertEquals(false,testBooleanMapCard.getMemory().get(c));
        charCardSubtypes.add(testBooleanMapCard);
        //Player map memory check
        ColourPlayerMap_CharCard testPlayerMapCard=new ColourPlayerMap_CharCard(testId,testCost,testPlayerMap);
        assertEquals(testPlayerMap,testPlayerMapCard.getMemory());
        testPlayerMap.put(c,p);
        assertNotEquals(testPlayerMap,testPlayerMapCard.getMemory());
        testPlayerMapCard.setMemory(testPlayerMap);
        assertEquals(testPlayerMap,testPlayerMapCard.getMemory());
        assertEquals(p,testPlayerMapCard.getMemory().get(c));
        testPlayerMapCard.resetMemory();
        assertNull(testPlayerMapCard.getMemory().get(c));
        charCardSubtypes.add(testPlayerMapCard);
        //Integer memory check
        Int_CharCard testIntCard=new Int_CharCard(testId,testCost,testInteger);
        assertEquals(testInteger,testIntCard.getMemory());
        testInteger++;
        testIntCard.setMemory(testInteger);
        assertEquals(testInteger,testIntCard.getMemory());
        charCardSubtypes.add(testIntCard);
        //Checking basic character card attributes
        for (CharacterCard card : charCardSubtypes) {
            assertEquals(testId,card.getCardID());
            assertEquals(testCost,card.getCost());
        }

    }
    //TODO: test memory for all subtypes instead
    /*
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
     */
}
