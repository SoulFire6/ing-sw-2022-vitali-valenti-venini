package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.CharacterCardNotFoundException;
import it.polimi.softeng.exceptions.InsufficientResourceException;
import it.polimi.softeng.model.Game;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Bag_Tile;
import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.CharacterCardSubTypes.ColourBooleanMap_CharCard;
import it.polimi.softeng.model.CharacterCardSubTypes.ColourPlayerMap_CharCard;
import it.polimi.softeng.model.CharacterCardSubTypes.Int_CharCard;
import it.polimi.softeng.model.CharacterCardSubTypes.StudentDisk_CharCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

public class CharCardController {
    private static final String CARD_DATA_PATH="src/main/resources/CardData/CharacterCards.csv";

    private enum CharID {
        MONK,HERALD,MAGICPOSTMAN,GRANDMAHERBS,CENTAUR,JESTER,KNIGHT,SHROOMVENDOR,MINSTREL,SPOILEDPRINCESS,THIEF,FARMER;
        //describes whether character card is in current game, this is for extra security
        private boolean inPlay;
        //describes whether character card has been played this turn
        private boolean active;
        CharID() {
            this.active=false;
            this.inPlay=false;
        }
    }

    public CharID getCharCardID(CharacterCard card) {
        try {
            return CharID.valueOf(card.getCardID().toUpperCase());
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
    }
    public void activateCard(Player p,String charID, Game game) throws CharacterCardNotFoundException, InsufficientResourceException {
        CharacterCard playedCard = null;
        for (CharacterCard card : game.getCharacterCards()) {
            if (card.getCardID().equals(charID)) {
                playedCard = card;
                break;
            }
        }
        if (playedCard == null) {
            throw new CharacterCardNotFoundException("Character with id " + charID + " is not in play");
        }
        if (p.getSchoolBoard().getCoins() < playedCard.getCost()) {
            throw new InsufficientResourceException("Not enough coins (" + p.getSchoolBoard().getCoins() + "/" + playedCard.getCost() + ")");
        }
        p.getSchoolBoard().setCoins(p.getSchoolBoard().getCoins()-playedCard.getCost());
        getCharCardID(playedCard).active=true;
        playedCard.incrementCost();
    }

    //Deactivates all cards (to be use at end of turn when effect for all cards ends)
    public void deactivateAllCards(ArrayList<CharacterCard> cards) {
        for (CharID id: CharID.values()) {
            if (id.inPlay && id.active) {
                id.active=false;
            }
        }
        CharID id;
        for (CharacterCard card: cards) {
            id=getCharCardID(card);
            if (id==CharID.SHROOMVENDOR) {
                ((ColourBooleanMap_CharCard)card).resetMemory();
            }
            if (id==CharID.FARMER) {
                ((ColourPlayerMap_CharCard)card).resetMemory();
            }
        }
    }
    //Param cardName can be passed directly as a string for checks during turns, like during influence calculations
    public boolean getActiveStatus(String cardName) {
        try {
            CharID id=CharID.valueOf(cardName.toUpperCase());
            return id.inPlay && id.active;
        }
        catch (IllegalArgumentException iae) {
            return false;
        }
    }
    public ArrayList<CharacterCard> genNewCharacterCards(int num) {
        Random rand=new Random();
        ArrayList<CharacterCard> res=new ArrayList<>();
        String card;
        CharacterCard newCard,removedCard;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CARD_DATA_PATH));
            //Skipping header of csv file
            reader.readLine();
            while((card=reader.readLine())!=null){
                newCard=createCharacterCard(card.split(","));
                if (newCard!=null) {
                    res.add(newCard);
                }
            }
            reader.close();
            //Removing excess cards
            while (res.size()>num) {
                removedCard=res.get(rand.nextInt(res.size()));
                res.remove(removedCard);
                CharID.valueOf(removedCard.getCardID().toUpperCase()).inPlay=false;
            }
            if (res.size()==num) {
                return res;
            } else {
                return null;
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    TODO: determine if method is needed based on how serialised objects can be loaded from save
    public ArrayList<CharacterCard> genCharCardsFromSave(String PATH_TO_SAVE) {
        ArrayList<CharacterCard> res=new ArrayList<>();
        String card;
        CharacterCard newCard;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_SAVE));
            while((card=reader.readLine())!=null){
                newCard=createCharacterCard(card.split(","));
                if (newCard!=null) {
                    res.add(newCard);
                }
            }
            return res;
        }
        catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    */
    public boolean checkDisabledColour(Colour c, ArrayList<CharacterCard> cards) {
        ColourBooleanMap_CharCard ShroomVendor=null;
        for (CharacterCard card: cards) {
            if (getCharCardID(card)==CharID.SHROOMVENDOR) {
                ShroomVendor=(ColourBooleanMap_CharCard) card;
            }
        }
        return getActiveStatus("SHROOMVENDOR") && ShroomVendor!=null && ShroomVendor.getMemory().get(c);
    }
    //Factory method for generating cards (divided based on memory needs)
    public CharacterCard createCharacterCard(String[] card) {
        try {
            CharID id=CharID.valueOf(card[0].toUpperCase());
            int cost=Integer.parseInt(card[1]);
            id.inPlay=true;
            switch (id) {
                //No memory
                case HERALD:
                case MAGICPOSTMAN:
                case CENTAUR:
                case KNIGHT:
                case MINSTREL:
                case THIEF:
                    return new CharacterCard(card[0],cost);
                //Stores amount of students disks on card
                case MONK:
                case JESTER:
                case SPOILEDPRINCESS:
                    return new StudentDisk_CharCard(card[0],cost,Colour.genIntegerMap());
                //Stores the amount of no entry tiles left on the card
                case GRANDMAHERBS:
                    return new Int_CharCard(card[0],cost,4);
                //Stores which colours do not count during influence calculation this turn
                case SHROOMVENDOR:
                    return new ColourBooleanMap_CharCard(card[0],cost,Colour.genBooleanMap());
                //Stores previous owners of professors that were taken
                case FARMER:
                    return new ColourPlayerMap_CharCard(card[0],cost,Colour.genPlayerMap());
                default:
                    System.out.println("This should not be reachable");
                    return null;
            }
        }
        catch (IllegalArgumentException iae) {
            System.out.println("Value "+card[0]+" not a char id or "+card[1]+" is not an int");
            return null;
        }
    }
    public void setUpCards(Bag_Tile b, ArrayList<CharacterCard> cards) {
        CharID id;
        EnumMap<Colour,Integer> students;
        for (CharacterCard card: cards) {
            id=CharID.valueOf(card.getCardID().toUpperCase());
            switch (id) {
                case MONK:
                case SPOILEDPRINCESS:
                    ((StudentDisk_CharCard)card).setMemory(b.drawStudents(4));
                    break;
                case JESTER:
                    ((StudentDisk_CharCard)card).setMemory(b.drawStudents(6));
                    break;
                default:
                    break;
            }
        }
    }
}
