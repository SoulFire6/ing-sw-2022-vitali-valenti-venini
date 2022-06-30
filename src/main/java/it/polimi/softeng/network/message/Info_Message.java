package it.polimi.softeng.network.message;

/**
 * This class represents an Info message
 */
public class Info_Message extends Message {
    private final String info;

    /**
     * @param infoType the MsgType of the info message
     * @param sender the sender of the message
     * @param context String explanation of the move
     * @param info String containing the info of the message
     * @see MsgType
     */
    Info_Message(MsgType infoType, String sender, String context, String info) {
        super(infoType,sender,context);
        this.info=info;
    }

    /**
     * @return String info of the message
     */
    public String getInfo() {
        return this.info;
    }
}
