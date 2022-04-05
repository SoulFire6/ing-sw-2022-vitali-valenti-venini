package it.polimi.softeng.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class CharacterCard {
    private static final String CARD_DATA_PATH="src/main/resources/CardData/CharacterCards.csv";
    private final String cardID;
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
        String cardValue;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CARD_DATA_PATH));
            //Skipping header of csv file
            reader.readLine();

            while((cardValue= reader.readLine())!=null){
                card = cardValue.split(",");

                res.add(new CharacterCard(card[0],Integer.parseInt(card[1])));
            }
            reader.close();
            //Removing excess cards
            while (res.size()>num) {
                res.remove(rand.nextInt(res.size()));
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
