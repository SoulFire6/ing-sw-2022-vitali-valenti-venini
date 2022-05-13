package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Cloud_Tile;
import it.polimi.softeng.model.ReducedModel.ReducedCharacterCard;
import it.polimi.softeng.model.ReducedModel.ReducedCloud;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

public class CharCard_Load_Msg extends Load_Message {
    private final ArrayList<ReducedCharacterCard> load;
    public CharCard_Load_Msg(String sender, String context, ArrayList<CharacterCard> load) {
        super(MsgType.CHARACTERCARDS,sender,context);
        this.load=simplifyChars(load);
    }
    public ArrayList<ReducedCharacterCard> getLoad() {
        return this.load;
    }
    private ArrayList<ReducedCharacterCard> simplifyChars(ArrayList<CharacterCard> cards) {
        ArrayList<ReducedCharacterCard> reducedCards=new ArrayList<>();
        for (CharacterCard card : cards) {
            reducedCards.add(new ReducedCharacterCard(card));
        }
        return reducedCards;
    }
}
