package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CharCardController
 */

public class CharCardControllerTest {
    CharCardController charController;
    ArrayList<CharacterCard> cards;

    /**
     * Tests character card generation
     */

    @Test @BeforeEach
    public void testGenCards() {
        int testNum=3;
        charController=new CharCardController();
        cards=charController.genNewCharacterCards(testNum,null);
        assertEquals(testNum,cards.size());
    }

    /**
     * Tests generating more cards than possible, resulting in null
     */

    @Test
    public void testGenNullCards() {
        cards=charController.genNewCharacterCards(13,null);
        assertNull(cards);
    }

    /**
     * Tests activating character card
     */

    @Test
    public void testActivateCardAndStatus() {
        Player player=new Player("test", Team.WHITE);
        ArrayList<String> playerNames=new ArrayList<>();
        playerNames.add(player.getName());
        playerNames.add("test 2");
        CharacterCard validCard=null;
        ArrayList<CharID> validIDs=new ArrayList<>(Arrays.asList(CharID.CENTAUR,CharID.KNIGHT,CharID.MAGIC_POSTMAN));
        LobbyController testController;
        try {
            do {
                testController=new LobbyController(playerNames,true,null,null);
                ArrayList<CharacterCard> cards=new ArrayList<> (testController.getGame().getCharacterCards());
                for (CharacterCard card : cards) {
                    if (validIDs.contains(card.getCharacter())) {
                        validCard = card;
                        break;
                    }
                }
            }while(validCard==null);
            final LobbyController controller=testController;
            final CharacterCard card=validCard;
            int initialCost=validCard.getCost();
            player.setSchoolBoard(new SchoolBoard_Tile("test",0,0,0,null,0));
            assertThrows(InsufficientResourceException.class,()->charController.findAndCheckCard(player,card.getCardID(),controller.getGame().getCharacterCards()));
            player.getSchoolBoard().setCoins(initialCost);
            assertDoesNotThrow(()->charController.findAndCheckCard(player,card.getCardID(),controller.getGame().getCharacterCards()));
            assertEquals(card,charController.findAndCheckCard(player,card.getCardID(),controller.getGame().getCharacterCards()));
            player.getSchoolBoard().setLastUsedCard(new AssistantCard("test",0,0));
            assertDoesNotThrow(()->charController.playCharacterCard(card,player,"".split(""),controller));
            assertEquals(0,player.getSchoolBoard().getCoins());
            assertEquals(initialCost+1,card.getCost());
            assertThrows(CharacterCardNotFoundException.class,()->charController.findAndCheckCard(player,"illegal card id",controller.getGame().getCharacterCards()).getCharacter().activateCard(player,"".split(""),controller));
            assertTrue(card.isActive());
        }
        catch (InvalidPlayerNumException | CharacterCardNotFoundException | InsufficientResourceException |
               MoveNotAllowedException ipne) {
            fail();
        }
    }

    /**
     * Tests correct deactivation of cards
     */

    @Test
    public void testDeactivateAllCards() {
        charController=new CharCardController();
        cards=charController.genNewCharacterCards(12,new Bag_Tile(10));
        for (CharacterCard card : cards) {
            card.setActive(true);
            assertTrue(card.isActive());
        }
        charController.deactivateAllCards(cards,null);
        for (CharacterCard card : cards) {
            assertFalse(card.isActive());
        }
    }

    /**
     * Tests shroom vendor effect
     */

    @Test
    public void testDisabledColour() {
        Colour c=Colour.getRandomColour();
        ArrayList<String> names=new ArrayList<>(Arrays.asList("1","2"));
        LobbyController controller=null;
        CharacterCard shroomVendor=null;
        while (shroomVendor==null) {
            try {
                controller=new LobbyController(names,true,"testController",null);
                for (CharacterCard card: controller.getGame().getCharacterCards()) {
                    if (card.getCharacter().equals(CharID.SHROOM_VENDOR)) {
                        shroomVendor=card;
                        controller.getGame().getPlayers().get(0).getSchoolBoard().setCoins(shroomVendor.getCost());
                        break;
                    }
                }
            }
            catch (InvalidPlayerNumException ipne) {
                fail();
            }
        }
        final LobbyController lobbyController=controller;
        final CharacterCard shroomCard=shroomVendor;
        EnumMap<Colour,Boolean> disabledColourMap=Colour.genBooleanMap();
        disabledColourMap.put(c,true);
        assertDoesNotThrow(()->charController.playCharacterCard(shroomCard,lobbyController.getGame().getPlayers().get(0), "red".split(" "),lobbyController));
        shroomCard.getCharacter().setMemory(disabledColourMap);
        assertTrue(shroomCard.isActive());
        assertTrue(shroomCard.getCharacter().getMemory(Boolean.class).get(c));
    }

    /**
     * Tests activating argument-less cards
     */

    @Test
    public void testCardSetup() {
        ArrayList<String> names=new ArrayList<>();
        boolean cardsWithSetup=false;
        names.add("test");
        names.add("test2");
        LobbyController controller=null;
        while (!cardsWithSetup) {
            try {
                controller = new LobbyController(names,true,"test",null);
                for (CharacterCard card: controller.getGame().getCharacterCards()) {
                    if (card.getCardID().equals("Monk") || card.getCardID().equals("SpoiledPrincess") || card.getCardID().equals("Jester")) {
                        cardsWithSetup=true;
                        break;
                    }
                }
            }
            catch (InvalidPlayerNumException ipne) {
                fail();
            }
        }
        int testFillAmount=controller.getGame().getBag().getFillAmount();
        charController=controller.getCharCardController();
        charController.setUpCards(controller.getGame().getBag(),controller.getGame().getCharacterCards());
        assertNotEquals(testFillAmount,controller.getGame().getBag().getFillAmount());
    }
}
