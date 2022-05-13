package it.polimi.softeng.network.message.load;

import it.polimi.softeng.model.AssistantCard;
import it.polimi.softeng.model.CharacterCard;
import it.polimi.softeng.model.ReducedModel.ReducedAssistantCard;
import it.polimi.softeng.model.ReducedModel.ReducedCharacterCard;
import it.polimi.softeng.network.message.MsgType;

import java.util.ArrayList;

public class AssistCard_Load_Msg extends Load_Message {
    private final ArrayList<ReducedAssistantCard> load;
    public AssistCard_Load_Msg(String sender, String context, ArrayList<AssistantCard> load) {
        super(MsgType.ASSISTANTCARDS,sender,context);
        this.load=simplifyAssistants(load);
    }
    public ArrayList<ReducedAssistantCard> getLoad() {
        return this.load;
    }
    private ArrayList<ReducedAssistantCard> simplifyAssistants(ArrayList<AssistantCard> cards) {
        ArrayList<ReducedAssistantCard> reducedCards=new ArrayList<>();
        for (AssistantCard card : cards) {
            reducedCards.add(new ReducedAssistantCard(card));
        }
        return reducedCards;
    }
}
