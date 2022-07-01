package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedPlayer;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

/**
 * This class represents a load message, used when the players need to be loaded.
 */
public class Players_Load_Msg extends Load_Message {
    private final ArrayList<ReducedPlayer> load;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load ArrayList of Player, containing the players to be simplified
     */
    public Players_Load_Msg(String sender, String context, ArrayList<Player> load) {
        super(MsgType.PLAYERS,sender, context);
        this.load=simplifyPlayers(load);
    }

    /**
     * @return ArrayList of ReducedPlayer, containing the reduced players
     */
    public ArrayList<ReducedPlayer> getLoad() {
        return this.load;
    }

    /**
     * @param players ArrayList<Player> containing the player to reduce
     * @return ArrayList<ReducedPlayer> containing the reduced version of the players
     */
    private ArrayList<ReducedPlayer> simplifyPlayers(ArrayList<Player> players) {
        ArrayList<ReducedPlayer> reducedPlayers=new ArrayList<>();
        for (Player player : players) {
            reducedPlayers.add(new ReducedPlayer(player));
        }
        return reducedPlayers;
    }
}
