package it.polimi.softeng.network.message;

import it.polimi.softeng.model.*;
import it.polimi.softeng.network.reducedModel.ReducedGame;
import it.polimi.softeng.network.reducedModel.ReducedTurnState;
import it.polimi.softeng.network.message.command.*;
import it.polimi.softeng.network.message.load.*;

import java.util.ArrayList;

/**
 * This class is responsible for the generation of messages with its static method genMessage
 */
//Message factory
public class MessageCenter {
    /**
     * This static factory method is responsible for the generation of a Message based on the given type.
     * @param type MsgType of the message
     * @param sender String sender of the message
     * @param context String explanation of the message
     * @param load Object representing the message
     * @return Message the generated message
     * @see MsgType
     */
    public static Message genMessage(MsgType type, String sender, String context, Object load) {
        switch (type.getMainType()) {
            //Sender is client/server, context describes who it is sent to or from, load is the message
            case INFO:
                return new Info_Message(type,sender,context,(String)load);
            case COMMAND:
                switch (type) {
                    case PLAYASSISTCARD:
                        return new AssistCard_Cmd_Msg(sender,context,(String)load);
                    case DISKTOISLAND:
                        return new DiskToIsland_Cmd_Msg(sender,context,(Colour)load,context);
                    case DISKTODININGROOM:
                        return new DiskToDiningRoom_Cmd_Msg(sender,context,(Colour)load);
                    case MOVEMN:
                        return new MoveMotherNature_Cmd_Msg(sender,context,(Integer)load);
                    case CHOOSECLOUD:
                        return new ChooseCloud_Cmd_Msg(sender,context,(String)load);
                    case PLAYCHARCARD:
                        return new CharCard_Cmd_Msg(sender,context,(String)load);
                }
            //Used for receiving model data from server, sender should always be server, context is data reference (ie what is and where to insert the data), load is object
            case LOAD:
                switch (type) {
                    case GAME:
                        return new Game_Load_Msg(sender,context,(ReducedGame) load);
                    case ISLANDS:
                        return new Island_Load_Msg(sender,context,(ArrayList<Island_Tile>)load);
                    case CLOUDS:
                        return new Cloud_Load_Msg(sender,context,(ArrayList<Cloud_Tile>) load);
                    case BAG:
                        return new Bag_Load_Msg(sender,context,(Bag_Tile) load);
                    case PLAYER:
                        return new Player_Load_Msg(sender,context,(Player) load);
                    case PLAYERS:
                        return new Players_Load_Msg(sender,context,(ArrayList<Player>) load);
                    case CHARACTERCARDS:
                        return new CharCard_Load_Msg(sender,context,(ArrayList<CharacterCard>) load);
                    case COINS:
                        return new Coin_Load_Msg(sender,context,(Integer)load);
                    case TURNSTATE:
                        return new TurnState_Load_Msg(sender,context,(ReducedTurnState) load);
                    default:
                        System.out.println("Wrong format");
                        return null;
                }
            default:
                return null;
        }
    }
}
