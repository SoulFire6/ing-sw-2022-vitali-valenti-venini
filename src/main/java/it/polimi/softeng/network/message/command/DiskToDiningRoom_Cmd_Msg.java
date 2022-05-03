package it.polimi.softeng.network.message.command;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.message.MsgType;

public class DiskToDiningRoom_Cmd_Msg extends Command_Message {
    Colour colour;
    DiskToDiningRoom_Cmd_Msg(String sender, String context, Colour colour) {
        super(sender,context,MsgType.CmdType.DISKTODININGROOM);
        this.colour=colour;
    }
    public Colour getColour() {
        return colour;
    }
}
