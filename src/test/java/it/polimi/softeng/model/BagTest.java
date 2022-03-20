package it.polimi.softeng.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class BagTest {
    @Test()
    public void testBagConstructor() {
        Integer initValue=0;
        Bag b=new Bag();
        for (Colour c: Colour.values()) {
            assertEquals("Bag was not initialised correctly",initValue,b.getContents().get(c));
        }
    }



}