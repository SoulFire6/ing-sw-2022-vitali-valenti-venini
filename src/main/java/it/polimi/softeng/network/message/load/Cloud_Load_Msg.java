package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.Cloud_Tile;
import it.polimi.softeng.model.ReducedModel.ReducedCloud;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

public class Cloud_Load_Msg extends Load_Message{
    private final ArrayList<ReducedCloud> load;
    public Cloud_Load_Msg(String sender, String context, ArrayList<Cloud_Tile> load) {
        super(MsgType.CLOUDS,sender,context);
        this.load=simplifyClouds(load);
    }
    public ArrayList<ReducedCloud> getLoad() {
        return this.load;
    }
    private ArrayList<ReducedCloud> simplifyClouds(ArrayList<Cloud_Tile> clouds) {
        ArrayList<ReducedCloud> reducedClouds=new ArrayList<>();
        for (Cloud_Tile cloud : clouds) {
            reducedClouds.add(new ReducedCloud(cloud));
        }
        return reducedClouds;
    }
}
