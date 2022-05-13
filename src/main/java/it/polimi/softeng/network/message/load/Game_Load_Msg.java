package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Game;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.MsgType;

public class Game_Load_Msg extends Load_Message{
    private final ReducedGame load;
    public Game_Load_Msg(String sender, String context, Game load) {
        super(MsgType.GAME,sender,context);
        this.load=new ReducedGame(load);
    }
    public ReducedGame getLoad() {
        return this.load;
    }
}
