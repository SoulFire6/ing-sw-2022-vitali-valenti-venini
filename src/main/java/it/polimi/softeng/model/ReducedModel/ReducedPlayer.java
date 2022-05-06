package it.polimi.softeng.model.ReducedModel;

import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.Team;

import java.io.Serializable;

public class ReducedPlayer implements Serializable {
    private final String name;
    private final Team team;
    private final ReducedSchoolBoard schoolBoard;

    public ReducedPlayer(Player player) {
        this.name=player.getName();
        this.team=player.getTeam();
        this.schoolBoard=new ReducedSchoolBoard(player.getSchoolBoard());
    }

    public String getName() {
        return name;
    }
    public Team getTeam() {
        return this.team;
    }
    public ReducedSchoolBoard getSchoolBoard() {
        return this.schoolBoard;
    }
}
