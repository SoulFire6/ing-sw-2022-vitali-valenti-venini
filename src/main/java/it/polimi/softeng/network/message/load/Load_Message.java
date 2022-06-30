package it.polimi.softeng.network.message.load;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

/**
 * This abstract class represents a generic load message
 */
public abstract class Load_Message extends Message {
    Load_Message(MsgType type, String sender,String context) {
        super(type,sender,context);
    }

    /**
     * @return Object load of the message
     */
    public abstract Object getLoad();
}
