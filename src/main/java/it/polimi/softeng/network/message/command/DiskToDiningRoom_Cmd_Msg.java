package it.polimi.softeng.network.message.command;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a command message, used when a disk is moved to the dining room
 */
public class DiskToDiningRoom_Cmd_Msg extends Command_Message {
    final Colour colour;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param colour Colour of the student disk that is being moved
     */
    public DiskToDiningRoom_Cmd_Msg(String sender, String context, Colour colour) {
        super(MsgType.DISKTODININGROOM,sender,context);
        this.colour=colour;
    }

    /**
     * @return Colour of the student disk that is being moved
     */
    public Colour getColour() {
        return colour;
    }
}
