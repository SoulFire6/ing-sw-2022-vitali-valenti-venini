package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Controller class that handles the generation and the activation of character cards
 */

public class CharCardController {
    private static final String CARD_DATA_PATH="/CardData/CharacterCards.csv";

    /**
     * This method is used to check if the character card is present and can be activated by a player
     * @param p the player who wants to perform the operation
     * @param cardID the ID of the character card that needs to be found
     * @param cards the list of all the usable cards of the current Game
     * @exception CharacterCardNotFoundException when the character card is not present in the current game
     * @exception InsufficientResourceException when the player doesn't have enough resources to activate the card
     * @exception MoveNotAllowedException when the action is requested in a not allowed state of the game
     * @exception GameIsOverException when the game is already over
     * @return boolean true if the operation succeeded, false otherwise
     */
    public CharacterCard findAndCheckCard(Player p,String cardID, ArrayList<CharacterCard> cards) throws CharacterCardNotFoundException, InsufficientResourceException, MoveNotAllowedException, GameIsOverException {
        Optional<CharacterCard> playedCard = cards.stream().filter(characterCard -> characterCard.getCardID().equalsIgnoreCase(cardID.replace("_"," "))).findFirst();
        if (playedCard.isEmpty()) {
            throw new CharacterCardNotFoundException("Character with id " + cardID + " is not in play");
        }
        if (p.getSchoolBoard().getCoins() < playedCard.get().getCost()) {
            throw new InsufficientResourceException("Not enough coins (" + p.getSchoolBoard().getCoins() + "/" + playedCard.get().getCost() + ")");
        }
        if (playedCard.get().isActive()) {
            throw new MoveNotAllowedException("Card is already active");
        }
        return playedCard.get();
    }

    /**
     * This method is used to play one CharacterCard
     * @param card the CharacterCard that needs to be played
     * @param player the Player who wants to activate the card
     * @param charArgs any parameter needed for the activation of a specific CharacterCard
     * @param controller the LobbyController object of the current game
     * @exception GameIsOverException when the game is already over
     * @exception MoveNotAllowedException when the action is requested in a not allowed state of the game
     */
    public void playCharacterCard(CharacterCard card, Player player, String[] charArgs, LobbyController controller) throws GameIsOverException,MoveNotAllowedException {
        card.getCharacter().activateCard(player,charArgs,controller);
        player.getSchoolBoard().setCoins(player.getSchoolBoard().getCoins()-card.getCost());
        card.setActive(true);
        card.incrementCost();
    }
    //Deactivates all cards (to be used at end of turn when effect for all cards ends)
    /**
     * This method set all CharacterCards active attribute to false and resets memory of certain cards
     * @param cards all the character cards that are in the current game
     * @param players players of the current game
     */
    public void deactivateAllCards(ArrayList<CharacterCard> cards, ArrayList<Player> players) {
        for (CharacterCard card : cards) {
            card.setActive(false);
            switch (card.getCharacter().getMemType()) {
                case BOOLEAN_COLOUR_MAP:
                    card.getCharacter().setMemory(Colour.genBooleanMap());
                    break;
                case PLAYER_COLOUR_MAP:
                    if (players!=null) {
                        EnumMap<Colour,String> mem=card.getCharacter().getMemory(String.class);
                        if (mem!=null) {
                            for (Colour c : Colour.values()) {
                                for (Player p: players) {
                                    p.getSchoolBoard().setProfessor(c, p.getName().equals(mem.get(c)));
                                }
                            }
                        }

                    }
                    card.getCharacter().setMemory(Colour.genStringMap());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * This method generates <b>num</b> random character cards
     * @param num the amount of character card to generate
     * @param bag the current game's Bag
     * @return Arraylist of CharacterCards
     */
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
    /**
     * This method initialize the character cards based on their type
     * @param b the Bag of the current game
     * @param cards the character cards that need to be initialized
     */
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

    /**
     * This method returns active status of requested card
     * @param id the card id that is requested
     * @param cards the character cards in the game
     * @return boolean true if card is active and in gamew
     */
    public boolean getActiveStatus(CharID id, ArrayList<CharacterCard> cards) {
        for (CharacterCard card : cards) {
            if (card.getCharacter().equals(id)) {
                return card.isActive();
            }
        }
        return false;
    }
}
