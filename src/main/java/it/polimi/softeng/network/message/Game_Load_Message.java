package it.polimi.softeng.network.message;

import it.polimi.softeng.model.Game;

public class Game_Load_Message extends Load_Message{
    private final Game load;
    Game_Load_Message(String sender, String context, Game load) {
        super(MsgType.LoadType.GAME,sender,context);
        this.load=load;
    }
    public Game getLoad() {
        return this.load;
    }
}
