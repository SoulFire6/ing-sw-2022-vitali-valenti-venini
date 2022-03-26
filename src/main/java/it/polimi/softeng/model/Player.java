package it.polimi.softeng.model;

public class Player {
    private String name;
    private Team team;
    private Player teamMate;
    private SchoolBoard_Tile schoolBoard;

    //Player constructor
    public Player(String name, Team team) {
        this.name=name;
        this.team=team;
    }
    public String getName() {
        return name;
    }
    public Team getTeam() {
        return this.team;
    }
    public Player getTeamMate() {
        return teamMate;
    }
    public void setTeamMate(Player teamMate) {
        this.teamMate=teamMate;
    }
    public SchoolBoard_Tile getSchoolBoard() {
        return schoolBoard;
    }
}
