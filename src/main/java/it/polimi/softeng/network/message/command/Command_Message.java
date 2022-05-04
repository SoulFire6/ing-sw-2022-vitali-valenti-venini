package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

public abstract class Command_Message extends Message {
    Command_Message(MsgType type, String sender, String context) {
        super(type,sender,context);
    }
}
