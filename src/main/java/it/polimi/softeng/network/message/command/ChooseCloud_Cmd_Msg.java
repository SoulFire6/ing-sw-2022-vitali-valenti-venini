package it.polimi.softeng.network.message.command;

import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a command message, used when a Cloud_Tile has been chosen
 */
public class ChooseCloud_Cmd_Msg extends Command_Message {
    String cloudID;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param cloudID String ID of the Cloud_tile that has been chosen
     */
    public ChooseCloud_Cmd_Msg(String sender, String context, String cloudID) {
        super( MsgType.CHOOSECLOUD,sender,context);
        this.cloudID=cloudID;
    }

    /**
     * @return String id of the cloud
     */
    public String getCloudID() {
        return cloudID;
    }
}
