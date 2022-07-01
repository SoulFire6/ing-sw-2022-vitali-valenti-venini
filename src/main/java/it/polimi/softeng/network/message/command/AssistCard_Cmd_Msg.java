package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a command message, used when an assistant card is played.
 */
public class AssistCard_Cmd_Msg extends Command_Message {
    final String assistID;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param assistID String ID of the assistant card that is being played
     */
    public AssistCard_Cmd_Msg(String sender, String context, String assistID) {
        super(MsgType.PLAYASSISTCARD,sender,context);
        this.assistID=assistID;
    }

    /**
     * @return String assistant card ID that is being played
     */
    public String getAssistID() {
        return assistID;
    }
}
