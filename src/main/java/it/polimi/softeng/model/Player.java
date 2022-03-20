package it.polimi.softeng.model;

public class Player {
    private String name;
    private Player teamMate=null;
    private boolean currentTurn;
    private SchoolBoard schoolBoard;

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
