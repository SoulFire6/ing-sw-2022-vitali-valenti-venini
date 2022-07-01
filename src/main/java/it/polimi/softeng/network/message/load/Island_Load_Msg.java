package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Island_Tile;
import it.polimi.softeng.network.reducedModel.ReducedIsland;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

/**
 * This class represents a load message, used when the islands need to be loaded.
 */
public class Island_Load_Msg extends Load_Message {
    private final ArrayList<ReducedIsland> load;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load ArrayList of Island_Tile, containing the island tiles to be simplified
     */
    public Island_Load_Msg(String sender, String context, ArrayList<Island_Tile> load) {
        super(MsgType.ISLANDS,sender,context);
        this.load=simplifyIslands(load);
    }

    /**
     * @return ArrayList of ReducedIsland, containing the reduced island tiles
     */
    public ArrayList<ReducedIsland> getLoad() {
        return this.load;
    }

    /**
     * @param islands ArrayList<Island_Tile> containing the island tiles to be reduced
     * @return ArrayList<ReducedIsland> the reduced island tiles
     */
    private ArrayList<ReducedIsland> simplifyIslands(ArrayList<Island_Tile> islands) {
        ArrayList<ReducedIsland> reducedIslands=new ArrayList<>();
        for (Island_Tile island : islands) {
            reducedIslands.add(new ReducedIsland(island));
        }
        return reducedIslands;
    }
}
