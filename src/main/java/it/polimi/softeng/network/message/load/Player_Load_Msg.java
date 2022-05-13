package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedPlayer;
import it.polimi.softeng.network.message.MsgType;

public class Player_Load_Msg extends Load_Message {
    private final ReducedPlayer load;
    public Player_Load_Msg(String sender, String context, Player load) {
        super(MsgType.PLAYER,sender,context);
        this.load=new ReducedPlayer(load);
    }
    public ReducedPlayer getLoad() {
        return this.load;
    }
}
