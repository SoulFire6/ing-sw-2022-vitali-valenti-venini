package it.polimi.softeng.controller;

import it.polimi.softeng.model.AssistantCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AssistantCardController {
    private static final String CARD_DATA_PATH="src/main/resources/CardData/AssistantCards.csv";
    public static ArrayList<AssistantCard> genHand(String PATH_TO_SAVE) {
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
}
