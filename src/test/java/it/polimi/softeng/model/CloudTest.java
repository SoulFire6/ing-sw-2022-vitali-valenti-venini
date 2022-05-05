package it.polimi.softeng.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
