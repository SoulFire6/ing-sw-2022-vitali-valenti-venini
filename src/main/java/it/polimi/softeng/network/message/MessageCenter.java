package it.polimi.softeng.network.message;

import it.polimi.softeng.model.Game;

//Message factory
public class MessageCenter {
    public Message genMessage(MsgType type, MsgType.LoadType loadType, String sender, String context, Object load) {
        switch (type) {
            //Sender is client/server, context describes who it is sent to or from, load is the message
            case INFO:
                return new Info_Message(sender,context,(String)load);
            case LOAD:
                //Used for receiving model data from server, sender should always be server, context is data reference (ie what is and where to insert the data), load is object
                switch (loadType) {
                    case GAME:
                        return new Game_Load_Message(sender,context,(Game)load);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
