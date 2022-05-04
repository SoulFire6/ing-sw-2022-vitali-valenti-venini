package it.polimi.softeng.network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private final MsgType subType;
    private final String sender;
    private final String context;

    public Message(MsgType subType, String sender, String context) {
        this.subType=subType;
        this.sender=sender;
        this.context=context;
    }

    public MsgType.MainType getType() {
        return this.subType.getMainType();
    }
    public MsgType getSubType() {
        return this.subType;
    }
    public String getSender() {
        return sender;
    }
    public String getContext() {
        return context;
    }
}
