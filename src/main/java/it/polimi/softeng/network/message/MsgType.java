package it.polimi.softeng.network.message;

public enum MsgType {
    //TEXT: some text to display or for when strings are passed to server (Info message)
    //LOAD: sends objects to load
    //INPUT: Info Message declaring end of server messages, awaiting client response
    //WHISPER: Send private message
    //COMMAND: Sends game command
    //CONNECT: client sends this along with username
    //DISCONNECT: Client or server has disconnected (abrupt disconnects send null messages instead)
    //CLOSE: Server/Lobby is closing, disconnect
    TEXT,LOAD,INPUT,WHISPER,COMMAND,ERROR,CONNECT,DISCONNECT,CLOSE;
    public enum LoadType {
        GAME,ISLAND,CLOUD,BAG,PLAYER,SCHOOLBOARD,CHARACTERCARD,ASSISTANTCARD
    }
    public enum CmdType {
        ASSISTCARD, DISKTOISLAND, DISKTODININGROOM, MOVEMN, CHOOSECLOUD, CHARCARD
    }
}
