package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

public class CharCard_Cmd_Msg extends Command_Message {
    String charID;
    CharCard_Cmd_Msg(String sender, String context, String charID) {
        super(sender,context, MsgType.CmdType.CHARCARD);
        this.charID=charID;
    }
    public String getCharID() {
        return charID;
    }
}
