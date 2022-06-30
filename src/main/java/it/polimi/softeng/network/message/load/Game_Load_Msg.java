package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Game;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a load message, used when the game needs to be loaded.
 */
public class Game_Load_Msg extends Load_Message{
    private final ReducedGame load;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load the reduced game to be loaded
     */
    public Game_Load_Msg(String sender, String context, ReducedGame load) {
        super(MsgType.GAME,sender,context);
        this.load=load;
    }

    /**
     * @return the reduced version of the game
     */
    public ReducedGame getLoad() {
        return this.load;
    }
}
