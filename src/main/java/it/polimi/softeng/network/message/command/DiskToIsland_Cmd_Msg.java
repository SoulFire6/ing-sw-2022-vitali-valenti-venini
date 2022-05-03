package it.polimi.softeng.network.message.command;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.message.MsgType;

public class DiskToIsland_Cmd_Msg extends Command_Message {
    Colour colour;
    String islandID;
    DiskToIsland_Cmd_Msg(String sender, String context, Colour colour, String islandID) {
        super(sender,context, MsgType.CmdType.DISKTOISLAND);
        this.colour=colour;
        this.islandID=islandID;
    }
    public Colour getColour() {
        return colour;
    }
    public String getIslandID() {
        return this.islandID;
    }

}
