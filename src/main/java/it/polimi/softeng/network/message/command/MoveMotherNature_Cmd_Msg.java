package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

public class MoveMotherNature_Cmd_Msg extends Command_Message {
    int moveAmount;
    public MoveMotherNature_Cmd_Msg(String sender, String context, int moveAmount) {
        super(MsgType.MOVEMN, sender,context);
        this.moveAmount=moveAmount;
    }
    public int getMoveAmount() {
        return moveAmount;
    }
}