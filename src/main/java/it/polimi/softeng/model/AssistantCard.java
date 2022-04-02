package it.polimi.softeng.model;

import java.io.File;
import java.io.FileNotFoundException;
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
        try {
            Scanner scanner = new Scanner(new File(CARD_DATA_PATH));
            scanner.useDelimiter("\n");
            //Skipping header of csv file
            scanner.next();
            while(scanner.hasNext()){
                card=scanner.next().split(",");
                res.add(new AssistantCard(card[0],Integer.valueOf(card[1]),Integer.valueOf(card[2])));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
