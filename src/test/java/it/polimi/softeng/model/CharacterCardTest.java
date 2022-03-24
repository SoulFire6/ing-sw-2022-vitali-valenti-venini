package it.polimi.softeng.model;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CharacterCardTest {
    @Test
    public void testGenCharacterCards() {
        Integer num=3;
        ArrayList<CharacterCard> testCards=CharacterCard.genCharacterCards(num);
        assertEquals("Character cards were not generated successfully (number generated differs from wanted)",num,Integer.valueOf(testCards.size()));
    }
}
