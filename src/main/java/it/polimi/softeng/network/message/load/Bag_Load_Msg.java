package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Bag_Tile;
import it.polimi.softeng.model.ReducedModel.ReducedBag;
import it.polimi.softeng.network.message.MsgType;

public class Bag_Load_Msg extends Load_Message{
    private final ReducedBag load;
    public Bag_Load_Msg(String sender, String context, Bag_Tile load) {
        super(MsgType.BAG,sender,context);
        this.load=new ReducedBag(load);
    }
    public ReducedBag getLoad() {
        return this.load;
    }
}
