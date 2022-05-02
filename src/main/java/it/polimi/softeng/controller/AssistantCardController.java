package it.polimi.softeng.controller;

import it.polimi.softeng.controller.Exceptions.AssistantCardNotFoundException;
import it.polimi.softeng.controller.Exceptions.MoveNotAllowedException;
import it.polimi.softeng.controller.Exceptions.NotYourTurnException;
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

    public void playAssistantCard(Player p, AssistantCard assistantCard, TurnManager turnManager) throws NotYourTurnException, AssistantCardNotFoundException, MoveNotAllowedException {

        if(p != turnManager.getCurrentPlayer())
            throw new NotYourTurnException("Can't execute this command, it's not the turn of player " + p.getName());
        if(!p.getSchoolBoard().getHand().contains(assistantCard))
            throw new AssistantCardNotFoundException("Error. Card " + assistantCard.getCardID() + "can't be found in " + p.getName() + "'s hand");
        if(turnManager.getTurnState()!= TurnManager.TurnState.ASSISTANT_CARDS_PHASE)
            throw new MoveNotAllowedException("Error. Operation not allowed");

        if(p.getSchoolBoard().getHand().size()>1)       //if the Player p hasn't just one card we check cards played by other players in this round
        {
            ArrayList<Player> previousPlayers = (ArrayList<Player>) turnManager.getPlayerOrder().subList(0,turnManager.getPlayerOrder().indexOf(p));
            for(Player player : previousPlayers)
                if(player.getSchoolBoard().getLastUsedCard().getTurnValue() == assistantCard.getTurnValue())
                    System.out.println("Error. Play another card");
        }

        p.getSchoolBoard().playAssistantCard(assistantCard.getCardID());
        turnManager.nextAction();
    }

}
