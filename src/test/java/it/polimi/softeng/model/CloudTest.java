package it.polimi.softeng.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CloudTest {
    @Test
    public void testCloudConstructor() {
        String testID="test";
        int testMaxSlots=5;
        Cloud_Tile cloud=new Cloud_Tile(testID,testMaxSlots);
        assertEquals(testID,cloud.getTileID());
        assertEquals(testMaxSlots,cloud.getMaxSlots());
    }
    @Test
    public void testSetMaxSlots() {
        int testMax=3;
        Cloud_Tile cloud=new Cloud_Tile("test",0);
        assertEquals(0,cloud.getMaxSlots());
        cloud.setMaxSlots(testMax);
        assertEquals(testMax,cloud.getMaxSlots());
    }
    @Test
    public void testFillCloud() {
        int fillAmount=5;
        int maxSlots=3;
        Bag_Tile bag=new Bag_Tile(fillAmount);
        fillAmount=bag.getFillAmount();
        Cloud_Tile cloud=new Cloud_Tile("test",maxSlots);
        cloud.fillCloud(bag);
        assertEquals(maxSlots,cloud.getFillAmount());
        assertEquals(fillAmount-maxSlots,bag.getFillAmount());
    }
    //TODO: move to controller test
    /*
    @Test
    public void testGenClouds() {
        int cloudNum=10;
        int maxSlots=2;
        ArrayList<Cloud_Tile> clouds=Cloud_Tile.genClouds(cloudNum,maxSlots);
        assertEquals(cloudNum,clouds.size());
        for (Cloud_Tile cloud: clouds) {
            assertEquals(maxSlots,cloud.getMaxSlots());
        }
    }
     */
}
