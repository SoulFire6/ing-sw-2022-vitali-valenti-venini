package it.polimi.softeng.network.message;

public abstract class Message {
    private final MsgType type;
    private final String sender;
    private final String context;

    public Message(MsgType type, String sender, String context) {
        this.type=type;
        this.sender=sender;
        this.context=context;
    }

    public MsgType getType() {
        return this.type;
    }
    public String getSender() {
        return sender;
    }
    public String getContext() {
        return context;
    }
}
