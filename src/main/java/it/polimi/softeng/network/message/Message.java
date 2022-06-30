package it.polimi.softeng.network.message;

import java.io.Serializable;

/**
 * This abstract class represents a generic message
 */
public abstract class Message implements Serializable {
    private final MsgType subType;
    private final String sender;
    private final String context;

    /**
     * @param subType MsgType of the message
     * @param sender the sender of the message
     * @param context String explanation of the move
     * @see MsgType
     */
    public Message(MsgType subType, String sender, String context) {
        this.subType=subType;
        this.sender=sender;
        this.context=context;
    }

    /**
     * @return MainType of the message
     * @see it.polimi.softeng.network.message.MsgType.MainType
     */
    public MsgType.MainType getType() {
        return this.subType.getMainType();
    }

    /**
     * @return MsgType subtype of the message
     * @see MsgType
     */
    public MsgType getSubType() {
        return this.subType;
    }

    /**
     * @return String sender of the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * @return String context, explanation of the message
     */
    public String getContext() {
        return context;
    }
}
