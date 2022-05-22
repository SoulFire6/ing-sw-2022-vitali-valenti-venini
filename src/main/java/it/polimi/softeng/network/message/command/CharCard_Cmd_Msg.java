package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

public class CharCard_Cmd_Msg extends Command_Message {
    String charID;

    String options;
    public CharCard_Cmd_Msg(String sender, String charID, String options) {
        super(MsgType.PLAYCHARCARD,sender,sender+" played "+charID);
        this.charID=charID;
        this.options=options;
    }
    public String getCharID() {
        return this.charID;
    }

    public Object getOptions() {
        return this.options;
    }
}
