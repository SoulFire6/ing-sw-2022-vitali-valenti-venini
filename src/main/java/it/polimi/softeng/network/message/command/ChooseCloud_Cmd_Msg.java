package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

public class ChooseCloud_Cmd_Msg extends Command_Message {
    String cloudID;
    ChooseCloud_Cmd_Msg(String sender, String context, String cloudID) {
        super(sender,context, MsgType.CmdType.CHOOSECLOUD);
        this.cloudID=cloudID;
    }
    public String getCloudID() {
        return cloudID;
    }
}
