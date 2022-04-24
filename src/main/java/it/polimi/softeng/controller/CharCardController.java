package it.polimi.softeng.controller;

import it.polimi.softeng.model.CharacterCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    public boolean activateCard(CharacterCard card) {
        try {
            CharID id=CharID.valueOf(card.getCardID().toUpperCase());
            if (id.inPlay && !id.active) {
                id.active=true;
                return true;
            } else {
                return false;
            }
        }
        catch (IllegalArgumentException iae) {
            return false;
        }
    }
    //Deactivates all cards (to be use at end of turn when effect for all cards ends)
    public void deactivateAllCards() {
        for (CharID id: CharID.values()) {
            if (id.inPlay && id.active) {
                id.active=false;
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
    //Factory method for generating cards
    public CharacterCard createCharacterCard(String[] card) {
        try {
            CharID id=CharID.valueOf(card[0].toUpperCase());
            int cost=Integer.parseInt(card[1]);
            id.inPlay=true;
            switch (id) {
                case MONK:
                case HERALD:
                case MAGICPOSTMAN:
                case GRANDMAHERBS:
                case CENTAUR:
                case JESTER:
                case KNIGHT:
                case SHROOMVENDOR:
                case MINSTREL:
                case SPOILEDPRINCESS:
                case THIEF:
                case FARMER:
                    //temp return value
                    return new CharacterCard(card[0],cost);
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
}
