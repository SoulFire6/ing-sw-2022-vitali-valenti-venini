package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.Cloud_Tile;
import it.polimi.softeng.model.ReducedModel.ReducedCharacterCard;
import it.polimi.softeng.model.ReducedModel.ReducedCloud;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

/**
 This class represents a load message, used when the character cards need to be loaded.
 */
public class CharCard_Load_Msg extends Load_Message {
    private final ArrayList<ReducedCharacterCard> load;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load ArrayList<CharacterCard> containing the character cards to be loaded
     */
    public CharCard_Load_Msg(String sender, String context, ArrayList<CharacterCard> load) {
        super(MsgType.CHARACTERCARDS,sender,context);
        this.load=simplifyChars(load);
    }

    /**
     * @return ArrayList<ReducedCharacterCard> containing the reduced versions of the character cards
     */
    public ArrayList<ReducedCharacterCard> getLoad() {
        return this.load;
    }

    /**
     * @param cards the character cards to be simplified
     * @return ArrayList<ReducedCharacterCard> containing the simplified versions of the character cards
     */
    private ArrayList<ReducedCharacterCard> simplifyChars(ArrayList<CharacterCard> cards) {
        ArrayList<ReducedCharacterCard> reducedCards=new ArrayList<>();
        for (CharacterCard card : cards) {
            reducedCards.add(new ReducedCharacterCard(card));
        }
        return reducedCards;
    }
}
