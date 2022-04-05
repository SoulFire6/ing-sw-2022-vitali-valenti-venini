package it.polimi.softeng.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AssistantCard {
    private static final String CARD_DATA_PATH="src/main/resources/CardData/AssistantCards.csv";
    private String cardID;
    private Integer turnValue;
    private Integer motherNatureValue;

    public AssistantCard(String id, Integer turn, Integer motherNature) {
        this.cardID=id;
        this.turnValue=turn;
        this.motherNatureValue=motherNature;
    }
    public String getCardID() {
        return this.cardID;
    }
    public Integer getTurnValue() {
        return this.turnValue;
    }
    public Integer getMotherNatureValue() {
        return this.motherNatureValue;
    }
    public static ArrayList<AssistantCard> genHand() {
        ArrayList<AssistantCard> res=new ArrayList<>();
        String[] card;
        String cardValue;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(CARD_DATA_PATH)));
            //Skipping header of csv file
            reader.readLine();
            while((cardValue= reader.readLine())!=null){
                card=cardValue.split(",");
                res.add(new AssistantCard(card[0],Integer.valueOf(card[1]),Integer.valueOf(card[2])));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
