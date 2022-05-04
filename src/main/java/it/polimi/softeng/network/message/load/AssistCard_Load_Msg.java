package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.network.message.MsgType;

public class AssistCard_Load_Msg extends Load_Message {
    private final AssistantCard load;
    public AssistCard_Load_Msg(String sender, String context, AssistantCard load) {
        super(MsgType.ASSISTANTCARD,sender,context);
        this.load=load;
    }
    public AssistantCard getLoad() {
        return this.load;
    }
}
