package it.polimi.softeng.network.message;

public abstract class Load_Message extends Message{
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
