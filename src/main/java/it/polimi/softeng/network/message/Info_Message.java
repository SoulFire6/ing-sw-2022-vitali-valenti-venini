package it.polimi.softeng.network.message;

public class Info_Message extends Message {
    private final String info;
    Info_Message(String sender, String context, String info) {
        super(MsgType.INFO,sender,context);
        this.info=info;
    }
    public String getInfo() {
        return this.info;
    }
}
