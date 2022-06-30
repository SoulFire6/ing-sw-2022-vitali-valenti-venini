package it.polimi.softeng.model;

/**
 * This class it's part of the model and represents its players
 */
public class Player {
    private final String name;
    private final Team team;
    private Player teamMate;
    private SchoolBoard_Tile schoolBoard;

    /**
     * @param name String name of the player
     * @param team Team of the player
     * @see Team
     */
    //Player constructor
    public Player(String name, Team team) {
        this.name=name;
        this.team=team;
    }

    /**
     * @return String name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * @return Team player's team
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * @return Player the teammate of this player. Null if 2 or 3 players game.
     */
    public Player getTeamMate() {
        return teamMate;
    }

    /**
     * This method will be called for both the teammates (only in 4 players games)
     * @param teamMate Player that will be set to be the teammate of the Player on which this method gets called on
     */
    public void setTeamMate(Player teamMate) {
        this.teamMate=teamMate;
    }

    /**
     * @param schoolBoard the schoolBoard to be set
     */
    public void setSchoolBoard(SchoolBoard_Tile schoolBoard) {
        this.schoolBoard=schoolBoard;
    }

    /**
     * @return this player's schoolBoard
     */
    public SchoolBoard_Tile getSchoolBoard() {
        return schoolBoard;
    }
}
