package it.polimi.softeng.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class CharacterCard {
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
    public static ArrayList<CharacterCard> genCharacterCards() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/main/resources/CardData/AssistantCards.csv"));
        scanner.useDelimiter(",");
        while(scanner.hasNext()){
            System.out.print(scanner.next()+",");
        }
        scanner.close();
        //TODO: Add cards from csv file

        ArrayList<CharacterCard> res=new ArrayList<>();
        res.add(new CharacterCard("Example",0));
        return res;
    }
}
