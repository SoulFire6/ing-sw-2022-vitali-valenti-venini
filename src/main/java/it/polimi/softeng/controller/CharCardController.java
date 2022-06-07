package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.CharacterCardNotFoundException;
import it.polimi.softeng.exceptions.GameIsOverException;
import it.polimi.softeng.exceptions.InsufficientResourceException;
import it.polimi.softeng.exceptions.MoveNotAllowedException;
import it.polimi.softeng.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Random;

public class CharCardController {
    private static final String CARD_DATA_PATH="/CardData/CharacterCards.csv";
    public CharacterCard findAndCheckCard(Player p,String cardID, ArrayList<CharacterCard> cards) throws CharacterCardNotFoundException, InsufficientResourceException, MoveNotAllowedException, GameIsOverException {
        CharacterCard playedCard = null;
        for (CharacterCard card : cards) {
            if (card.getCardID().equals(cardID)) {
                playedCard = card;
                break;
            }
        }
        if (playedCard == null) {
            throw new CharacterCardNotFoundException("Character with id " + cardID + " is not in play");
        }
        if (p.getSchoolBoard().getCoins() < playedCard.getCost()) {
            throw new InsufficientResourceException("Not enough coins (" + p.getSchoolBoard().getCoins() + "/" + playedCard.getCost() + ")");
        }
        if (playedCard.isActive()) {
            throw new MoveNotAllowedException("Card is already active");
        }
        return playedCard;
    }

    public void playCharacterCard(CharacterCard card, Player player, String[] charArgs, LobbyController controller) throws GameIsOverException,MoveNotAllowedException {
        card.getCharacter().activateCard(player,charArgs,controller);
        player.getSchoolBoard().setCoins(player.getSchoolBoard().getCoins()-card.getCost());
        card.setActive(true);
        card.incrementCost();
    }
    //Deactivates all cards (to be used at end of turn when effect for all cards ends)
    public void deactivateAllCards(ArrayList<CharacterCard> cards, ArrayList<Player> players) {
        for (CharacterCard card : cards) {
            card.setActive(false);
            switch (card.getCharacter().getMemType()) {
                case BOOLEAN_COLOUR_MAP:
                    card.getCharacter().setMemory(Colour.genBooleanMap());
                    break;
                case PLAYER_COLOUR_MAP:
                    if (players!=null) {
                        EnumMap<Colour,Player> mem=card.getCharacter().getMemory(Player.class);
                        if (mem!=null) {
                            for (Colour c : Colour.values()) {
                                for (Player p: players) {
                                    p.getSchoolBoard().setProfessor(c, p.equals(mem.get(c)));
                                }
                            }
                        }

                    }
                    card.getCharacter().setMemory(Colour.genPlayerMap());
                    break;
                default:
                    break;
            }
        }
    }

    public ArrayList<CharacterCard> genNewCharacterCards(int num, Bag_Tile bag) {
        Random rand=new Random();
        ArrayList<CharacterCard> res=new ArrayList<>();
        String line;
        String[] card;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(CARD_DATA_PATH))));
            //Skipping header of csv file
            reader.readLine();
            while((line=reader.readLine())!=null){
                card=line.split(",");
                try {
                    res.add(new CharacterCard(card[0],Integer.parseInt(card[1]),CharID.valueOf(card[2])));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Removing excess cards
            while (res.size()>num) {
                res.remove(res.get(rand.nextInt(res.size())));
            }
            if (res.size()!=num) {
                return null;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bag!=null) {
            setUpCards(bag, res);
        }
        return res;
    }
    public void setUpCards(Bag_Tile b, ArrayList<CharacterCard> cards) {
        for (CharacterCard card: cards) {
            switch (card.getCharacter().getMemType()) {
                case INTEGER:
                    card.getCharacter().setMemory(card.getCharacter().getLimit());
                    break;
                case INTEGER_COLOUR_MAP:
                    card.getCharacter().setMemory(b.drawStudents(card.getCharacter().getLimit()));
                    break;
                default:
                    break;
            }
        }
    }

    public boolean getActiveStatus(CharID id, ArrayList<CharacterCard> cards) {
        for (CharacterCard card : cards) {
            if (card.getCharacter().equals(id)) {
                return card.isActive();
            }
        }
        return false;
    }
}
