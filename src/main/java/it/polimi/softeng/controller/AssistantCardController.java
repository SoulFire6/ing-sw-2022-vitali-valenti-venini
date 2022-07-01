package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.AssistantCardAlreadyPlayedException;
import it.polimi.softeng.exceptions.AssistantCardNotFoundException;
import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller class that handles the generation and the activation of assistant cards
 */
public class AssistantCardController {

    private static final String CARD_DATA_PATH="/CardData/AssistantCards.csv";

    /**
     * This method is used to generate a player's hand, composed of 10 assistant cards, by reading from a .csv file the values needed to generate them.
     * @return ArrayList of AssistantCard, This does return an ArrayList composed of 10 assistant cards
     */
    public ArrayList<AssistantCard> genHand() {
        ArrayList<AssistantCard> res=new ArrayList<>();
        String[] card;
        String cardValue;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(CARD_DATA_PATH))));
            //Skipping header of csv file
            reader.readLine();
            while((cardValue= reader.readLine())!=null){
                card=cardValue.split(",");
                res.add(new AssistantCard(card[0],Integer.parseInt(card[1]),Integer.parseInt(card[2])));
            }
            reader.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return res;
    }


    /**
     * This method is invoked when Player p requests to play a AssistantCard with ID assistID
     * @param assistID The ID of the AssistantCard the Player p wants to play
     * @param p The Player object representing the player wanting to play the assistant card
     * @param players The ArrayList of Player, containing all the players of the current match
     * @exception AssistantCardNotFoundException when the assistant card with the specified ID isn't found
     * @exception AssistantCardAlreadyPlayedException when the assistant card with the specified ID was already played in a previous turn
     */
    public void playAssistantCard(Player p, String assistID, ArrayList<Player> players) throws AssistantCardNotFoundException, AssistantCardAlreadyPlayedException {
        Optional<AssistantCard> playedCard=p.getSchoolBoard().getHand().stream().filter(assistantCard -> assistantCard.getCardID().equalsIgnoreCase(assistID)).findFirst();
        if (playedCard.isEmpty()) {
            throw new AssistantCardNotFoundException("Could not find assistant card with id "+ assistID);
        }
        ArrayList<String> previousPlayedCards= new ArrayList<>();
        if (p!=players.get(0)) {
            for (Player player : players) {
                if (player.getSchoolBoard().getLastUsedCard()!=null) {
                    previousPlayedCards.add(player.getSchoolBoard().getLastUsedCard().getCardID().toLowerCase());
                } else {
                    break;
                }
            }
        }
        if (p.getSchoolBoard().getHand().size()>1 && previousPlayedCards.contains(assistID.toLowerCase())) {
            throw new AssistantCardAlreadyPlayedException(assistID+" was already played this turn");
        }
        p.getSchoolBoard().playAssistantCard(playedCard.get().getCardID());
    }

}
