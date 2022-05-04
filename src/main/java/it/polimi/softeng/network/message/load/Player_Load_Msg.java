package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Player;
import it.polimi.softeng.network.message.MsgType;

public class Player_Load_Msg extends Load_Message {
    private final Player load;
    public Player_Load_Msg(String sender, String context, Player load) {
        super(MsgType.PLAYER,sender,context);
        this.load=load;
    }
    public Player getLoad() {
        return this.load;
    }
}
