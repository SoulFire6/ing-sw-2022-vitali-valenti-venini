package it.polimi.softeng.network.message.load;


import it.polimi.softeng.network.message.MsgType;

public class Coin_Load_Msg extends Load_Message {
    private final int coins;
    public Coin_Load_Msg(String sender, String context, int load) {
        super(MsgType.COINS,sender,context);
        coins=load;
    }
    public Integer getLoad() {
        return this.coins;
    }
}
