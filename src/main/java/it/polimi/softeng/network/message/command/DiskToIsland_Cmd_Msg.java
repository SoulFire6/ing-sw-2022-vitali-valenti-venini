package it.polimi.softeng.network.message.command;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a command message, used when a disk is moved to an island
 */
public class DiskToIsland_Cmd_Msg extends Command_Message {
    final Colour colour;
    final String islandID;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param colour Colour of the student disk that is being moved
     * @param islandID String ID of the island where the student disk is being moved
     */
    public DiskToIsland_Cmd_Msg(String sender, String context, Colour colour, String islandID) {
        super(MsgType.DISKTOISLAND,sender,context);
        this.colour=colour;
        this.islandID=islandID;
    }

    /**
     * @return Colour of the student disk that is being moved
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Getter for island id
     * @return String island id
     */
    public String getIslandID() {
        return this.islandID;
    }

}
