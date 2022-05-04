package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.AssistantCardAlreadyPlayedException;
import it.polimi.softeng.exceptions.AssistantCardNotFoundException;
import it.polimi.softeng.exceptions.MoveNotAllowedException;
import it.polimi.softeng.exceptions.NotYourTurnException;
import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AssistantCardController {
    private static final String CARD_DATA_PATH="src/main/resources/CardData/AssistantCards.csv";

    public ArrayList<AssistantCard> genHand(String PATH_TO_SAVE) {
        ArrayList<AssistantCard> res=new ArrayList<>();
        String[] card;
        String cardValue;
        BufferedReader reader;
        try {
            if (PATH_TO_SAVE==null) {
                reader = new BufferedReader(new FileReader(CARD_DATA_PATH));
            } else {
                reader = new BufferedReader(new FileReader(PATH_TO_SAVE));
            }
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

    public void playAssistantCard(Player p, String assistID, TurnManager turnManager) throws AssistantCardNotFoundException, AssistantCardAlreadyPlayedException {
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
        for (Player player: turnManager.getPlayerOrder().subList(0,turnManager.getPlayerOrder().indexOf(p))) {
            previousPlayedCards.add(player.getSchoolBoard().getLastUsedCard().getCardID());
        }
        if (p.getSchoolBoard().getHand().size()>1 && previousPlayedCards.contains(assistID)) {
            throw new AssistantCardAlreadyPlayedException(assistID+" was already played this turn");
        }
        p.getSchoolBoard().playAssistantCard(playedCard.getCardID());
        turnManager.nextAction();
    }

}
