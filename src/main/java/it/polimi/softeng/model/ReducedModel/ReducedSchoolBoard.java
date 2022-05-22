package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.SchoolBoard_Tile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;

public class ReducedSchoolBoard implements Serializable {
    private final EnumMap<Colour,Integer> entrance;
    private final EnumMap<Colour,Integer> diningRoom;
    private final EnumMap<Colour,Boolean> professorTable;
    private final int towers;
    private final ArrayList<ReducedAssistantCard> hand;
    private final ReducedAssistantCard lastUsedCard;
    private final int coins;

    public ReducedSchoolBoard(SchoolBoard_Tile schoolboard) {
        this.entrance=schoolboard.getContents();
        this.diningRoom=schoolboard.getDiningRoom();
        this.professorTable=schoolboard.getProfessorTable();
        this.towers=schoolboard.getTowers();
        this.hand=setHand(schoolboard.getHand());
        this.lastUsedCard=schoolboard.getLastUsedCard()==null?null:new ReducedAssistantCard(schoolboard.getLastUsedCard());
        this.coins=schoolboard.getCoins();
    }
    private ArrayList<ReducedAssistantCard> setHand(ArrayList<AssistantCard> hand) {
        ArrayList<ReducedAssistantCard> reducedHand=new ArrayList<>();
        for (AssistantCard card : hand) {
            reducedHand.add(new ReducedAssistantCard(card));
        }
        return reducedHand;
    }

    public EnumMap<Colour, Integer> getEntrance() {
        return this.entrance.clone();
    }
    public EnumMap<Colour, Integer> getDiningRoom() {
        return this.diningRoom.clone();
    }
    public EnumMap<Colour, Boolean> getProfessorTable() {
        return this.professorTable.clone();
    }
    public int getTowers() {
        return this.towers;
    }
    public ArrayList<ReducedAssistantCard> getHand() {
        return this.hand;
    }
    public ReducedAssistantCard getLastUsedCard() {
        return this.lastUsedCard;
    }
    public int getCoins() {
        return this.coins;
    }
}
