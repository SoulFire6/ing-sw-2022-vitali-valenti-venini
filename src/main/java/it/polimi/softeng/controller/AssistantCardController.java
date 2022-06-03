package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.AssistantCardAlreadyPlayedException;
import it.polimi.softeng.exceptions.AssistantCardNotFoundException;
import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedPlayer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AssistantCardController {
    private static final String CARD_DATA_PATH="src/main/resources/CardData/AssistantCards.csv";

    public ArrayList<AssistantCard> genHand() {
        ArrayList<AssistantCard> res=new ArrayList<>();
        String[] card;
        String cardValue;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(CARD_DATA_PATH));
            //Skipping header of csv file
            reader.readLine();
            while((cardValue= reader.readLine())!=null){
                card=cardValue.split(",");
                res.add(new AssistantCard(card[0],Integer.parseInt(card[1]),Integer.parseInt(card[2])));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void playAssistantCard(Player p, String assistID, ArrayList<Player> players) throws AssistantCardNotFoundException, AssistantCardAlreadyPlayedException {
        AssistantCard playedCard=null;
        for (AssistantCard card: p.getSchoolBoard().getHand()) {
            if (card.getCardID().equals(assistID)) {
                playedCard=card;
            }
        }
        if (playedCard==null) {
            throw new AssistantCardNotFoundException("Could not find assistant card with id "+ assistID);
        }
        ArrayList<String> previousPlayedCards= new ArrayList<>();
        if (p!=players.get(0)) {
            for (Player player : players) {
                if (player.getSchoolBoard().getLastUsedCard()!=null) {
                    previousPlayedCards.add(player.getSchoolBoard().getLastUsedCard().getCardID());
                } else {
                    break;
                }
            }
        }
        if (p.getSchoolBoard().getHand().size()>1 && previousPlayedCards.contains(assistID)) {
            throw new AssistantCardAlreadyPlayedException(assistID+" was already played this turn");
        }
        p.getSchoolBoard().playAssistantCard(playedCard.getCardID());
    }

}
