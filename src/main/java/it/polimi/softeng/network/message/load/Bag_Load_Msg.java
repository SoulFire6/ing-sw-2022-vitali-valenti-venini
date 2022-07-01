package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Bag_Tile;
import it.polimi.softeng.network.reducedModel.ReducedBag;
import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a load message, used when the bag needs to be loaded.
 */
public class Bag_Load_Msg extends Load_Message{
    private final ReducedBag load;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load Bag_Tile to get loaded
     */
    public Bag_Load_Msg(String sender, String context, Bag_Tile load) {
        super(MsgType.BAG,sender,context);
        this.load=new ReducedBag(load);
    }

    /**
     * @return ReducedBag the reduced version of the bag
     */
    public ReducedBag getLoad() {
        return this.load;
    }
}
