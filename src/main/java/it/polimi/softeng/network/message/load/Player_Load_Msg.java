package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedPlayer;
import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a load message, used when a player need to be loaded.
 */
public class Player_Load_Msg extends Load_Message {
    private final ReducedPlayer load;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load the Player to be reduced
     */
    public Player_Load_Msg(String sender, String context, Player load) {
        super(MsgType.PLAYER,sender,context);
        this.load=new ReducedPlayer(load);
    }

    /**
     * @return ReducedPlayer the reduced version of the Player
     */
    public ReducedPlayer getLoad() {
        return this.load;
    }
}
