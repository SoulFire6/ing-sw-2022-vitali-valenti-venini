package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Cloud_Tile;
import it.polimi.softeng.network.reducedModel.ReducedCloud;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

/**
 * This class represents a load message, used when the cloud tiles need to be loaded.
 */
public class Cloud_Load_Msg extends Load_Message{
    private final ArrayList<ReducedCloud> load;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load ArrayList of Cloud_Tile containing the cloud tiles to be loaded
     */
    public Cloud_Load_Msg(String sender, String context, ArrayList<Cloud_Tile> load) {
        super(MsgType.CLOUDS,sender,context);
        this.load=simplifyClouds(load);
    }

    /**
     * @return ArrayList of ReducedCloud, containing the reduced versions of the cloud tiles
     */
    public ArrayList<ReducedCloud> getLoad() {
        return this.load;
    }

    /**
     * @param clouds the cloud tiles to be simplified
     * @return ArrayList<ReducedCloud> containing the simplified version of the cloud tiles
     */
    private ArrayList<ReducedCloud> simplifyClouds(ArrayList<Cloud_Tile> clouds) {
        ArrayList<ReducedCloud> reducedClouds=new ArrayList<>();
        for (Cloud_Tile cloud : clouds) {
            reducedClouds.add(new ReducedCloud(cloud));
        }
        return reducedClouds;
    }
}
