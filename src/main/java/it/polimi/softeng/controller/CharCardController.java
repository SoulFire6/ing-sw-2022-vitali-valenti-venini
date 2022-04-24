package it.polimi.softeng.controller;

import it.polimi.softeng.model.Bag_Tile;
import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Player;

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
    public void deactivateAllCards(ArrayList<CharacterCard> cards) {
        for (CharID id: CharID.values()) {
            if (id.inPlay && id.active) {
                id.active=false;
            }
        }
        CharID id;
        for (CharacterCard card: cards) {
            id=CharID.valueOf(card.getCardID().toUpperCase());
            if (id==CharID.SHROOMVENDOR || id==CharID.FARMER) {
                card.setMemory(null);
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
                    return new CharacterCard(card[0],cost,Colour.genIntegerMap()) {
                        @Override
                        public EnumMap<Colour,Integer> getMemory() {
                            return (EnumMap<Colour, Integer>) this.memory;
                        }
                    };
                //Stores the amount of no entry tiles left on the card
                case GRANDMAHERBS:
                    return new CharacterCard(card[0],cost,Integer.valueOf(4)) {
                        @Override
                        public Integer getMemory() {
                            return (Integer) this.memory;
                        }
                    };
                //Stores which colours do not count during influence calculation this turn
                case SHROOMVENDOR:
                    return new CharacterCard(card[0],cost,Colour.genBooleanMap()) {
                        @Override
                        public EnumMap<Colour,Boolean> getMemory() {
                            return (EnumMap<Colour, Boolean>) this.memory;
                        }
                        @Override
                        public void setMemory(Object mem) {
                            this.memory=Colour.genBooleanMap();
                        }
                    };
                //Stores previous owners of professors that were taken
                case FARMER:
                    return new CharacterCard(card[0],cost,Colour.genPlayerMap()) {
                        @Override
                        public EnumMap<Colour, Player> getMemory() {
                            return (EnumMap<Colour, Player>) this.memory;
                        }
                        @Override
                        public void setMemory(Object mem) {
                            this.memory=Colour.genPlayerMap();
                        }
                    };
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
                    card.setMemory(b.drawStudents(4));
                    break;
                case JESTER:
                    card.setMemory(b.drawStudents(6));
                    break;
                default:
                    break;
            }
        }
    }
}
