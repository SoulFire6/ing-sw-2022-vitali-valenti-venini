package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.CharacterCardNotFoundException;
import it.polimi.softeng.exceptions.InsufficientResourceException;
import it.polimi.softeng.model.*;
import it.polimi.softeng.model.CharacterCardSubTypes.ColourBooleanMap_CharCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CharCardControllerTest {
    CharCardController charController;
    ArrayList<CharacterCard> cards;

    @Test @BeforeEach
    public void testGenCards() {
        int testNum=3;
        charController=new CharCardController();
        cards=charController.genNewCharacterCards(testNum);
        assertEquals(testNum,cards.size());
    }

    @Test
    public void testGenNullCards() {
        cards=charController.genNewCharacterCards(13);
        assertNull(cards);
    }

    @Test
    public void testGetCharID() {
        System.out.println(cards);
        charController.getCharCardID(cards.get(0));
    }

    @Test
    public void testActivateCardAndStatus() {
        Player player=new Player("test", Team.WHITE);
        ArrayList<String> playerNames=new ArrayList<>();
        playerNames.add(player.getName());
        playerNames.add("test 2");
        Game game=new LobbyController(playerNames,true,null).getGame();
        CharacterCard card=game.getCharacterCards().get(0);
        int initialCost=card.getCost();
        player.setSchoolBoard(new SchoolBoard_Tile("test",0,0,0,null,0));
        assertThrows(InsufficientResourceException.class,()->charController.activateCard(player,card.getCardID(),game));
        player.getSchoolBoard().setCoins(initialCost);
        assertDoesNotThrow(()->charController.activateCard(player,card.getCardID(),game));
        assertEquals(0,player.getSchoolBoard().getCoins());
        assertEquals(initialCost+1,card.getCost());
        assertThrows(CharacterCardNotFoundException.class,()->charController.activateCard(player,"illegal id",game));
        assertTrue(charController.getActiveStatus(card.getCardID()));
    }

    @Test
    public void testDeactivateAllCards() {
        charController=new CharCardController();
        cards=charController.genNewCharacterCards(12);
        charController.deactivateAllCards(cards);
    }

    @Test
    public void testDisabledColour() {
        Random rand=new Random();
        Colour c=Colour.values()[rand.nextInt(Colour.values().length)];
        ArrayList<String> names=new ArrayList<>(Arrays.asList("1","2"));
        LobbyController controller=null;
        CharacterCard shroomVendor=null;
        while (shroomVendor==null) {
            controller=new LobbyController(names,true,"testController");
            for (CharacterCard card: controller.getGame().getCharacterCards()) {
                if (card.getCardID().equals("ShroomVendor")) {
                    shroomVendor=card;
                    controller.getGame().getPlayers().get(0).getSchoolBoard().setCoins(shroomVendor.getCost());
                    break;
                }
            }
        }
        final LobbyController lobbyController=controller;
        final CharacterCard shroomCard=shroomVendor;
        EnumMap<Colour,Boolean> disabledColourMap=Colour.genBooleanMap();
        disabledColourMap.put(c,true);
        assertDoesNotThrow(()->charController.activateCard(lobbyController.getGame().getPlayers().get(0), shroomCard.getCardID(),lobbyController.getGame()));
        ((ColourBooleanMap_CharCard)shroomCard).setMemory(disabledColourMap);
        assertTrue(charController.getActiveStatus(shroomCard.getCardID()));
        assertTrue(charController.checkDisabledColour(c,controller.getGame().getCharacterCards()));
    }

    @Test
    public void testErrorGeneratingCard() {
        String[] testCard={"herald","not a number"};
        assertNull(charController.createCharacterCard(testCard));
        testCard[0]="not a char id";
        testCard[1]="1";
        assertNull(charController.createCharacterCard(testCard));
    }

    @Test
    public void testCardSetup() {
        ArrayList<String> names=new ArrayList<>();
        boolean cardsWithSetup=false;
        names.add("test");
        names.add("test2");
        LobbyController controller=null;
        while (!cardsWithSetup) {
            controller = new LobbyController(names,true,"test");
            for (CharacterCard card: controller.getGame().getCharacterCards()) {
                if (card.getCardID().equals("Monk") || card.getCardID().equals("SpoiledPrincess") || card.getCardID().equals("Jester")) {
                    cardsWithSetup=true;
                    break;
                }
            }
        }
        int testFillAmount=controller.getGame().getBag().getFillAmount();
        charController=controller.getCharCardController();
        charController.setUpCards(controller.getGame().getBag(),controller.getGame().getCharacterCards());
        assertNotEquals(testFillAmount,controller.getGame().getBag().getFillAmount());
    }
}
