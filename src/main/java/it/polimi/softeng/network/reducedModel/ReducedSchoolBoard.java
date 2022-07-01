package it.polimi.softeng.network.reducedModel;

import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.SchoolBoard_Tile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * This class is part of the reduced model and represents its school boards
 */
public class ReducedSchoolBoard implements Serializable {
    private final EnumMap<Colour,Integer> entrance;
    private final EnumMap<Colour,Integer> diningRoom;
    private final EnumMap<Colour,Boolean> professorTable;
    private final int towers;
    private final ArrayList<ReducedAssistantCard> hand;
    private final ReducedAssistantCard lastUsedCard;
    private final int coins;

    /**
     * This is the constructor. It initializes a reduced version of a SchoolBoard
     * @param schoolboard the schoolboard to be reduced
     */
    public ReducedSchoolBoard(SchoolBoard_Tile schoolboard) {
        this.entrance=schoolboard.getContents();
        this.diningRoom=schoolboard.getDiningRoom();
        this.professorTable=schoolboard.getProfessorTable();
        this.towers=schoolboard.getTowers();
        this.hand=setHand(schoolboard.getHand());
        this.lastUsedCard=schoolboard.getLastUsedCard()==null?null:new ReducedAssistantCard(schoolboard.getLastUsedCard());
        this.coins=schoolboard.getCoins();
    }

    /**
     * @param hand ArrayList of AssistantCard containing the assistant cards to set in this reduced school board's hand
     * @return ArrayList of ReducedAssistantCard containing the reduced assistant cards
     */
    private ArrayList<ReducedAssistantCard> setHand(ArrayList<AssistantCard> hand) {
        ArrayList<ReducedAssistantCard> reducedHand=new ArrayList<>();
        for (AssistantCard card : hand) {
            reducedHand.add(new ReducedAssistantCard(card));
        }
        return reducedHand;
    }

    /**
     * @return Colour EnumMap Colour of Integer, containing for each Colour the corresponding Integer number of student disks present in the entrance
     */
    public EnumMap<Colour, Integer> getEntrance() {
        return this.entrance.clone();
    }

    /**
     * @return Colour EnumMap of Integer, containing for each Colour the corresponding Integer number of student disks present in the dining room
     */
    public EnumMap<Colour, Integer> getDiningRoom() {
        return this.diningRoom.clone();
    }

    /**
     * @return Colour EnumMap Colour of Boolean, containing for each Colour a Boolean, true if the professor of this Colour is owned by this SchoolBoard's player, false otherwise
     */
    public EnumMap<Colour, Boolean> getProfessorTable() {
        return this.professorTable.clone();
    }

    /**
     * @return int number of towers available for this reduced school board
     */
    public int getTowers() {
        return this.towers;
    }

    /**
     * @return ArrayList of ReducedAssistantCard, the hand of assistant cards still available to be played
     */
    public ArrayList<ReducedAssistantCard> getHand() {
        return this.hand;
    }

    /**
     * @return ReducedAssistantCard the last assistant card played by this school board player
     */
    public ReducedAssistantCard getLastUsedCard() {
        return this.lastUsedCard;
    }

    /**
     * @return int number of coins available
     */
    public int getCoins() {
        return this.coins;
    }
}
