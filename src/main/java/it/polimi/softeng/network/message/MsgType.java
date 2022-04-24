package it.polimi.softeng.network.message;

public enum MsgType {
    INFO,LOAD;
    public enum LoadType {
        GAME,ISLAND,CLOUD,BAG,PLAYER,SCHOOLBOARD,CHARACTERCARD,ASSISTANTCARD
    }
}
