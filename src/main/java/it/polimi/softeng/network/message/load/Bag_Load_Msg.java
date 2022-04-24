package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Bag_Tile;
import it.polimi.softeng.network.message.MsgType;

public class Bag_Load_Msg extends Load_Message{
    private final Bag_Tile load;
    public Bag_Load_Msg(String sender, String context, Bag_Tile load) {
        super(MsgType.LoadType.BAG,sender,context);
        this.load=load;
    }
    public Bag_Tile getLoad() {
        return this.load;
    }
}
