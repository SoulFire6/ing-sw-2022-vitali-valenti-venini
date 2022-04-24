package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Game;
import it.polimi.softeng.network.message.MsgType;

public class Game_Load_Msg extends Load_Message{
    private final Game load;
    public Game_Load_Msg(String sender, String context, Game load) {
        super(MsgType.LoadType.GAME,sender,context);
        this.load=load;
    }
    public Game getLoad() {
        return this.load;
    }
}
