package it.polimi.softeng.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CharacterCard {
    private static final String CARD_DATA_PATH="src/main/resources/CardData/CharacterCards.csv";
    private String cardID;
    private Integer cost;

    public CharacterCard(String id,Integer cost) {
        this.cardID=id;
        this.cost=cost;
    }
    public String getCardID() {
        return this.cardID;
    }
    public Integer getCost() {
        return this.cost;
    }
    public void incrementCost() {
        this.cost+=1;
    }
    public static ArrayList<CharacterCard> genCharacterCards(Integer num) {
        Random rand=new Random();
        ArrayList<CharacterCard> res=new ArrayList<>();
        String[] card;
        try {
            Scanner scanner = new Scanner(new File(CARD_DATA_PATH));
            scanner.useDelimiter("\n");
            //Skipping header of csv file
            scanner.next();
            while(scanner.hasNext()){
                card=scanner.next().split(",");
                res.add(new CharacterCard(card[0],Integer.valueOf(card[1])));
            }
            scanner.close();
            //Removing excess cards
            while (res.size()>num) {
                res.remove(rand.nextInt(res.size()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
