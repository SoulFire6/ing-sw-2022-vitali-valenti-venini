package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

public class AssistCard_Cmd_Msg extends Command_Message {
    String assistID;
    public AssistCard_Cmd_Msg(String sender, String context, String assistID) {
        super(MsgType.PLAYASSISTCARD,sender,context);
        this.assistID=assistID;
    }
    public String getAssistID() {
        return assistID;
    }
}
