package it.polimi.softeng.network.message;

public enum MsgType {
    //Info messages
    TEXT(MainType.INFO),
    INPUT(MainType.INFO),
    WHISPER(MainType.INFO),
    CONNECT(MainType.INFO),
    DISCONNECT(MainType.INFO),
    ERROR(MainType.INFO),
    GAMEOVER(MainType.INFO),
    CLOSE(MainType.INFO),
    //Load messages
    GAME(MainType.LOAD),
    ISLAND(MainType.LOAD),
    CLOUD(MainType.LOAD),
    BAG(MainType.LOAD),
    PLAYER(MainType.LOAD),
    SCHOOLBOARD(MainType.LOAD),
    CHARACTERCARD(MainType.LOAD),
    ASSISTANTCARD(MainType.LOAD),
    //Command messages
    PLAYASSISTCARD(MainType.COMMAND),
    DISKTOISLAND(MainType.COMMAND),
    DISKTODININGROOM(MainType.COMMAND),
    MOVEMN(MainType.COMMAND),
    CHOOSECLOUD(MainType.COMMAND),
    PLAYCHARCARD(MainType.COMMAND);

    public enum MainType {
        INFO, LOAD, COMMAND
    }
    private final MainType mainType;
    MsgType(MainType mainType) {
        this.mainType=mainType;
    }
    public MainType getMainType() {
        return mainType;
    }
}
