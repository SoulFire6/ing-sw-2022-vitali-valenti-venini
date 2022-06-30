package it.polimi.softeng.network.message.load;


import it.polimi.softeng.network.message.MsgType;

/**
 * This class represents a load message, used when the coins need to be loaded.
 */
public class Coin_Load_Msg extends Load_Message {
    private final int coins;

    /**
     * @param sender String name of the player who requested the move
     * @param context String explanation of the move
     * @param load the number of coins to be loaded
     */
    public Coin_Load_Msg(String sender, String context, int load) {
        super(MsgType.COINS,sender,context);
        coins=load;
    }

    /**
     * @return Integer number of coins
     */
    public Integer getLoad() {
        return this.coins;
    }
}
