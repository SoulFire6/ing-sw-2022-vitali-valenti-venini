package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.SchoolBoard_Tile;
import it.polimi.softeng.network.message.MsgType;

public class SchoolBoard_Load_Msg extends Load_Message {
    private final SchoolBoard_Tile load;
    public SchoolBoard_Load_Msg(String sender, String context, SchoolBoard_Tile load) {
        super(MsgType.LoadType.SCHOOLBOARD,sender,context);
        this.load=load;
    }
    public SchoolBoard_Tile getLoad() {
        return this.load;
    }
}
