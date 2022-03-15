package it.polimi.softeng;

public class Player {
    String name;
    Player teamMate=null;
    boolean currentTurn;
    SchoolBoard schoolBoard;

    //Player constructor
    public Player(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public Player getTeamMate() {
        return teamMate;
    }

    public boolean isCurrentTurn() {
        return currentTurn;
    }

    public SchoolBoard getSchoolBoard() {
        return schoolBoard;
    }
}
