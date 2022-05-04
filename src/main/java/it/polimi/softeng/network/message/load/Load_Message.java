package it.polimi.softeng.network.message.load;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

public abstract class Load_Message extends Message {
    Load_Message(MsgType type, String sender,String context) {
        super(type,sender,context);
    }
    public abstract Object getLoad();
}
