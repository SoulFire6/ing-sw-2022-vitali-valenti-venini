package it.polimi.softeng.network.message;

/**
 * This Enum Class defines all types of messages, defining the MsgType and its MainType.
 */
public enum MsgType {
    //Info messages
    TEXT(MainType.INFO),
    INPUT(MainType.INFO),
    CLIENT_NUM(MainType.INFO),
    WHISPER(MainType.INFO),
    CONNECT(MainType.INFO),
    DISCONNECT(MainType.INFO),
    SAVE_AND_QUIT(MainType.INFO),
    ERROR(MainType.INFO),
    //Load messages
    GAME(MainType.LOAD),
    ISLANDS(MainType.LOAD),
    CLOUDS(MainType.LOAD),
    BAG(MainType.LOAD),
    PLAYER(MainType.LOAD),
    PLAYERS(MainType.LOAD),
    CHARACTERCARDS(MainType.LOAD),
    COINS(MainType.LOAD),
    TURNSTATE(MainType.LOAD),
    //Command messages
    PLAYASSISTCARD(MainType.COMMAND),
    DISKTOISLAND(MainType.COMMAND),
    DISKTODININGROOM(MainType.COMMAND),
    MOVEMN(MainType.COMMAND),
    CHOOSECLOUD(MainType.COMMAND),
    PLAYCHARCARD(MainType.COMMAND);

    /**
     * The MainType Enum describes if the MsgType is an Info message, a Load message or a Command message.
     */
    public enum MainType {
        INFO, LOAD, COMMAND
    }
    private final MainType mainType;

    /**
     * Constructor method
     * @param mainType the MainType of the message
     * @see MainType
     */
    MsgType(MainType mainType) {
        this.mainType=mainType;
    }

    /**
     * @return MainType main type of this message type
     */
    public MainType getMainType() {
        return mainType;
    }
}
