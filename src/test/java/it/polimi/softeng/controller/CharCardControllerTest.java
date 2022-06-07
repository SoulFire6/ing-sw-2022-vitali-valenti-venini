package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.CharacterCardNotFoundException;
import it.polimi.softeng.exceptions.InsufficientResourceException;
import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import it.polimi.softeng.model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CharCardControllerTest {
    CharCardController charController;
    ArrayList<CharacterCard> cards;

    @Test @BeforeEach
    public void testGenCards() {
        int testNum=3;
        charController=new CharCardController();
        cards=charController.genNewCharacterCards(testNum,null);
        assertEquals(testNum,cards.size());
    }

    @Test
    public void testGenNullCards() {
        cards=charController.genNewCharacterCards(13,null);
        assertNull(cards);
    }

    @Test
    public void testActivateCardAndStatus() {
        Player player=new Player("test", Team.WHITE);
        ArrayList<String> playerNames=new ArrayList<>();
        playerNames.add(player.getName());
        playerNames.add("test 2");
        LobbyController testController;
        try {
            do {
                testController=new LobbyController(playerNames,true,null,null);
                ArrayList<CharacterCard> cards=new ArrayList<> (testController.getGame().getCharacterCards());
                for (CharacterCard card : cards) {
                    switch (card.getCharacter()) {
                        case CENTAUR:
                        case KNIGHT:
                        case FARMER:
                        case MAGIC_POSTMAN:
                            break;
                        default:
                            testController.getGame().getCharacterCards().remove(card);
                            break;
                    }
                }
            }while(testController.getGame().getCharacterCards().size()==0);
            final LobbyController controller=testController;
            CharacterCard card=controller.getGame().getCharacterCards().get(0);
            int initialCost=card.getCost();
            player.setSchoolBoard(new SchoolBoard_Tile("test",0,0,0,null,0));
            assertThrows(InsufficientResourceException.class,()->charController.activateCard(player,card.getCardID(),"",controller));
            player.getSchoolBoard().setCoins(initialCost);
            assertDoesNotThrow(()->charController.activateCard(player,card.getCardID(),"",controller));
            assertEquals(0,player.getSchoolBoard().getCoins());
            assertEquals(initialCost+1,card.getCost());
            assertThrows(CharacterCardNotFoundException.class,()->charController.activateCard(player,"illegal id","",controller));
            assertTrue(card.isActive());
        }
        catch (InvalidPlayerNumException ipne) {
            fail();
        }
    }

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
        assertDoesNotThrow(()->charController.activateCard(lobbyController.getGame().getPlayers().get(0), shroomCard.getCardID(), "red",lobbyController));
        shroomCard.getCharacter().setMemory(disabledColourMap);
        assertTrue(shroomCard.isActive());
        //assertTrue(charController.checkDisabledColour(c,controller.getGame().getCharacterCards()));
    }

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
