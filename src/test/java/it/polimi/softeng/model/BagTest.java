package it.polimi.softeng.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class BagTest {
    //TODO finish tests
    @Test
    public void testBagConstructor() {
        Integer initValue=0;
        Bag_Tile b=new Bag_Tile(initValue);
        for (Colour c: Colour.values()) {
            assertEquals("Bag was not initialised correctly: "+c,initValue,b.getContents().get(c));
        }
    }



}