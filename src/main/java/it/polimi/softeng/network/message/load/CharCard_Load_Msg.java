package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.network.message.MsgType;

public class CharCard_Load_Msg extends Load_Message {
    private final CharacterCard load;
    public CharCard_Load_Msg(String sender, String context, CharacterCard load) {
        super(MsgType.LoadType.CHARACTERCARD,sender,context);
        this.load=load;
    }
    public CharacterCard getLoad() {
        return this.load;
    }
}
