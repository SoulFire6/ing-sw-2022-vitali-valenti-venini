package it.polimi.softeng.network.message;

import it.polimi.softeng.model.*;
import it.polimi.softeng.network.message.load.*;

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
                        return new Game_Load_Msg(sender,context,(Game)load);
                    case ISLAND:
                        return new Island_Load_Msg(sender,context,(Island_Tile)load);
                    case CLOUD:
                        return new Cloud_Load_Msg(sender,context,(Cloud_Tile) load);
                    case BAG:
                        return new Bag_Load_Msg(sender,context,(Bag_Tile) load);
                    case PLAYER:
                        return new Player_Load_Msg(sender,context,(Player) load);
                    case SCHOOLBOARD:
                        return new SchoolBoard_Load_Msg(sender,context,(SchoolBoard_Tile)load);
                    case CHARACTERCARD:
                        return new CharCard_Load_Msg(sender,context,(CharacterCard)load);
                    case ASSISTANTCARD:
                        return new AssistCard_Load_Msg(sender,context,(AssistantCard)load);
                    default:
                        System.out.println("Wrong format");
                        return null;
                }
            default:
                return null;
        }
    }
}
