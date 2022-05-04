package it.polimi.softeng.network.message.command;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.message.MsgType;

public class DiskToDiningRoom_Cmd_Msg extends Command_Message {
    Colour colour;
    public DiskToDiningRoom_Cmd_Msg(String sender, String context, Colour colour) {
        super(MsgType.DISKTODININGROOM,sender,context);
        this.colour=colour;
    }
    public Colour getColour() {
        return colour;
    }
}
