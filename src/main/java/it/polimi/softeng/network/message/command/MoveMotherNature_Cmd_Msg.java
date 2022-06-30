package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a command message, used when mother nature is going to move
 */
public class MoveMotherNature_Cmd_Msg extends Command_Message {
    int moveAmount;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param moveAmount int number of movements that mother nature is supposed to do
     */
    public MoveMotherNature_Cmd_Msg(String sender, String context, int moveAmount) {
        super(MsgType.MOVEMN, sender,context);
        this.moveAmount=moveAmount;
    }

    /**
     * @return int number of movements mother nature is supposed to do
     */
    public int getMoveAmount() {
        return moveAmount;
    }
}
