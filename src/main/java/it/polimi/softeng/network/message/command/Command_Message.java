package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

public abstract class Command_Message extends Message {
    MsgType.CmdType commandType;
    Command_Message(String sender, String context, MsgType.CmdType commandType) {
        super(MsgType.COMMAND,sender,context);
        this.commandType=commandType;
    }
    public MsgType.CmdType getCommandType() {
        return commandType;
    }
}
