package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Island_Tile;
import it.polimi.softeng.network.message.MsgType;

public class Island_Load_Msg extends Load_Message {
    private final Island_Tile load;
    public Island_Load_Msg(String sender, String context, Island_Tile load) {
        super(MsgType.LoadType.ISLAND,sender,context);
        this.load=load;
    }
    public Island_Tile getLoad() {
        return this.load;
    }
}
