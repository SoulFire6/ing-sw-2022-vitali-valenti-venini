package it.polimi.softeng.network.message.load;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

public abstract class Load_Message extends Message {
    private final MsgType.LoadType loadType;
    Load_Message(MsgType.LoadType loadType, String sender,String context) {
        super(MsgType.LOAD,sender,context);
        this.loadType=loadType;
    }
    public MsgType.LoadType getLoadType() {
        return this.loadType;
    }
    public abstract Object getLoad();
}
