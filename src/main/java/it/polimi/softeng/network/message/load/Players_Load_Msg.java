package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedPlayer;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

public class Players_Load_Msg extends Load_Message {
    private final ArrayList<ReducedPlayer> load;
    public Players_Load_Msg(String sender, String context, ArrayList<Player> load) {
        super(MsgType.PLAYERS,sender, context);
        this.load=simplifyPlayers(load);
    }
    public ArrayList<ReducedPlayer> getLoad() {
        return this.load;
    }
    private ArrayList<ReducedPlayer> simplifyPlayers(ArrayList<Player> players) {
        ArrayList<ReducedPlayer> reducedPlayers=new ArrayList<>();
        for (Player player : players) {
            reducedPlayers.add(new ReducedPlayer(player));
        }
        return reducedPlayers;
    }
}
