package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a command message, used when a character card is played.
 */
public class CharCard_Cmd_Msg extends Command_Message {
    String charID;

    String options;

    /**
     * @param sender String name of the player who requested the move
     * @param charID String ID of the character card that is being played
     * @param options the arguments to pass when activating card
     */
    public CharCard_Cmd_Msg(String sender, String charID, String options) {
        super(MsgType.PLAYCHARCARD,sender,sender+" played "+charID);
        this.charID=charID;
        this.options=options;
    }

    /**
     * @return String id of the character card
     */
    public String getCharID() {
        return this.charID;
    }

    /**
     * Getter for options
     * @return Object options
     */
    public Object getOptions() {
        return this.options;
    }
}
