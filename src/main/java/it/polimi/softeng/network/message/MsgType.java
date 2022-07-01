package it.polimi.softeng.network.message;

/**
 * This Enum Class defines all types of messages, defining the MsgType and its MainType.
 */
public enum MsgType {
    //Info messages
    /**
     * Defines a generic info message
     */
    TEXT(MainType.INFO),
    /**
     * Defines when server is actively asking for a value from a selection (whilst creating or joining a lobby)
     */
    INPUT(MainType.INFO),
    /**
     * Defines an update of the amount of connected clients
     */
    CLIENT_NUM(MainType.INFO),
    /**
     * Defines a player to player message
     */
    WHISPER(MainType.INFO),
    /**
     * Defines an initial connection message used to gather username
     */
    CONNECT(MainType.INFO),
    /**
     * Defines a decided upon disconnection either by client or when lobby closes due to GameOverException or LobbyClientDisconnectException (not an abrupt one)
     */
    DISCONNECT(MainType.INFO),
    /**
     * Defines a message requesting to save the game and close the lobby, can only be effectively used by lobby master
     */
    SAVE_AND_QUIT(MainType.INFO),
    /**
     * Defines an error message
     */
    ERROR(MainType.INFO),
    //Load messages
    /**
     * Defines a ReducedGame load
     */
    GAME(MainType.LOAD),
    /**
     * Defines an ArrayList of ReducedIsland load
     */
    ISLANDS(MainType.LOAD),
    /**
     * Defines an ArrayList of ReducedCloud load
     */
    CLOUDS(MainType.LOAD),
    /**
     * Defines a ReducedBag load
     */
    BAG(MainType.LOAD),
    /**
     * Defines an ArrayList of ReducedPlayer load
     */
    PLAYER(MainType.LOAD),
    /**
     * Defines a ReducedPlayer load
     */
    PLAYERS(MainType.LOAD),
    /**
     * Defines an ArrayList of ReducedCharacterCard load
     */
    CHARACTERCARDS(MainType.LOAD),
    /**
     * Defines a coin load
     */
    COINS(MainType.LOAD),
    /**
     * Defines a ReducedTurnstate load
     */
    TURNSTATE(MainType.LOAD),
    /**
     * Defines a command to play an assist card
     */
    //Command messages
    PLAYASSISTCARD(MainType.COMMAND),
    /**
     * Defines a command to move a disk to an island
     */
    DISKTOISLAND(MainType.COMMAND),
    /**
     * Defines a command to move a disk to a dining room
     */
    DISKTODININGROOM(MainType.COMMAND),
    /**
     * Defines a command to move mother nature
     */
    MOVEMN(MainType.COMMAND),
    /**
     * Defines a command to choose a cloud to refill from
     */
    CHOOSECLOUD(MainType.COMMAND),
    /**
     * Defines a command to play a character card
     */
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
