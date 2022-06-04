package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.ReducedModel.ReducedTurnState;
import it.polimi.softeng.network.message.MsgType;

public class TurnState_Load_Msg extends Load_Message {

    private final ReducedTurnState turnState;

    public TurnState_Load_Msg(String sender, String context, ReducedTurnState load) {
        super(MsgType.TURNSTATE,sender,context);
        turnState=load;
    }

    @Override
    public ReducedTurnState getLoad() {
        return this.turnState;
    }
}
