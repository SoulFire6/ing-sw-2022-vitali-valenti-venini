package it.polimi.softeng.network.message.load;

import it.polimi.softeng.network.reducedModel.ReducedTurnState;
import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a load message, used when the turn state needs to be loaded.
 */
public class TurnState_Load_Msg extends Load_Message {

    private final ReducedTurnState turnState;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load the reduced version of the turn state
     */
    public TurnState_Load_Msg(String sender, String context, ReducedTurnState load) {
        super(MsgType.TURNSTATE,sender,context);
        turnState=load;
    }

    /**
     * @return ReducedTurnState the reduced turn state
     */
    @Override
    public ReducedTurnState getLoad() {
        return this.turnState;
    }
}
