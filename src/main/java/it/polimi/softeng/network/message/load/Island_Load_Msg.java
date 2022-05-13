package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Island_Tile;
import it.polimi.softeng.model.ReducedModel.ReducedIsland;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

public class Island_Load_Msg extends Load_Message {
    private final ArrayList<ReducedIsland> load;
    public Island_Load_Msg(String sender, String context, ArrayList<Island_Tile> load) {
        super(MsgType.ISLANDS,sender,context);
        this.load=simplifyIslands(load);
    }
    public ArrayList<ReducedIsland> getLoad() {
        return this.load;
    }
    private ArrayList<ReducedIsland> simplifyIslands(ArrayList<Island_Tile> islands) {
        ArrayList<ReducedIsland> reducedIslands=new ArrayList<>();
        for (Island_Tile island : islands) {
            reducedIslands.add(new ReducedIsland(island));
        }
        return reducedIslands;
    }
}
