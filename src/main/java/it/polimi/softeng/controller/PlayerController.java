package it.polimi.softeng.controller;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.Team;

import java.util.ArrayList;

public class PlayerController {
    public static ArrayList<Colour> getTeamColours(Team t, ArrayList<Player> players) {
        ArrayList<Colour> colours=new ArrayList<>();
        for (Player p: players) {
            if (p.getTeam()==t) {
                for (Colour c: Colour.values()) {
                    if (p.getSchoolBoard().getProfessor(c) && !colours.contains(c)) {
                        colours.add(c);
                    }
                }
            }
        }
        return colours;
    }
}
