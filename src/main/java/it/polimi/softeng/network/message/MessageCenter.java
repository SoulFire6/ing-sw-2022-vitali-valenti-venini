package it.polimi.softeng.network.message;

//Message factory
public class MessageCenter {
    public Message genMessage(MsgType type) {
        switch (type) {
            //TODO: implement different message types
            case CONNECTED:
            case CLOSED:
            case BUSY:
            case CREATE:
            case JOIN:
            case LOAD:
            case UPDATE:
            case TXT:
            default:
                return null;
        }
    }
}
