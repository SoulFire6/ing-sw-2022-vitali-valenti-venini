package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.Team;

import java.io.Serializable;

/**
 * This class is part of the reduced model and represents its players
 */
public class ReducedPlayer implements Serializable {
    private final String name;
    private final Team team;
    private final ReducedSchoolBoard schoolBoard;

    /**
     * This is the constructor. It initializes a reduced version of a Player
     * @param player the player to be reduced
     */
    public ReducedPlayer(Player player) {
        this.name=player.getName();
        this.team=player.getTeam();
        this.schoolBoard=new ReducedSchoolBoard(player.getSchoolBoard());
    }

    /**
     * @return String the name of this reduced player
     */
    public String getName() {
        return name;
    }

    /**
     * @return Team the team of this reduced player
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * @return ReducedSchoolBoard the reduced version of this player's SchoolBoard
     */
    public ReducedSchoolBoard getSchoolBoard() {
        return this.schoolBoard;
    }
}
