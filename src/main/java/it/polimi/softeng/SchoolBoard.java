package it.polimi.softeng;

public class SchoolBoard {
    Integer[] entrance;
    Integer[] diningRoom;
    boolean[] professorTable;
    Integer towers;
    Integer coins;
    AssistantCard[] hand;

    public SchoolBoard() {
        this.entrance=new Integer[] {0,0,0,0,0};
        this.diningRoom=new Integer[] {0,0,0,0,0};
        this.professorTable=new boolean[] {false,false,false,false,false};
        this.towers=8;
        this.coins=0;
    }
}
