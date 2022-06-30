package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

/**
 * This abstract class represents a generic command message
 */
public abstract class Command_Message extends Message {
    /**
     * @param type MsgType of the command
     * @param sender String name of the sender
     * @param context explanation of the command that is happening
     * @see MsgType
     */
    Command_Message(MsgType type, String sender, String context) {
        super(type,sender,context);
    }
}
