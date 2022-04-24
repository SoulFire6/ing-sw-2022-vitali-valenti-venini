package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Cloud_Tile;
import it.polimi.softeng.network.message.MsgType;

public class Cloud_Load_Msg extends Load_Message{
    private final Cloud_Tile load;
    public Cloud_Load_Msg(String sender, String context, Cloud_Tile load) {
        super(MsgType.LoadType.CLOUD,sender,context);
        this.load=load;
    }
    public Cloud_Tile getLoad() {
        return this.load;
    }
}
