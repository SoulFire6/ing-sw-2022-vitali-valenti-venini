package it.polimi.softeng.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CloudTest {
    @Test
    public void testCloudConstructor() {
        String testID="test";
        Integer testMaxSlots=5;
        Cloud_Tile cloud=new Cloud_Tile(testID,testMaxSlots);
        assertEquals(testID,cloud.getTileID());
        assertEquals(testMaxSlots,cloud.getMaxSlots());
    }
    @Test
    public void testSetMaxSlots() {
        Integer testMax=3;
        Cloud_Tile cloud=new Cloud_Tile("test",0);
        assertEquals(0,cloud.getMaxSlots().intValue());
        cloud.setMaxSlots(testMax);
        assertEquals(testMax.intValue(),cloud.getMaxSlots().intValue());
    }
    @Test
    public void testFillCloud() {
        Integer fillAmount=5;
        Integer maxSlots=3;
        Bag_Tile bag=new Bag_Tile(fillAmount);
        fillAmount=bag.getFillAmount();
        Cloud_Tile cloud=new Cloud_Tile("test",maxSlots);
        cloud.fillCloud(bag);
        assertEquals(maxSlots,cloud.getFillAmount());
        assertEquals(fillAmount-maxSlots,bag.getFillAmount().intValue());
    }
    @Test
    public void testGenClouds() {
        Integer cloudNum=10;
        Integer maxSlots=2;
        ArrayList<Cloud_Tile> clouds=Cloud_Tile.genClouds(cloudNum,maxSlots);
        assertEquals(cloudNum.intValue(),clouds.size());
        for (Cloud_Tile cloud: clouds) {
            assertEquals(maxSlots,cloud.getMaxSlots());
        }
    }
}
