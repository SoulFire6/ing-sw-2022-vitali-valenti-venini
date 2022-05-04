package it.polimi.softeng.network.server;

import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LobbyListener implements Runnable {
    private final LobbyClient client;
    private final ConcurrentLinkedQueue<Message> messageQueue;
    LobbyListener(LobbyClient lobbyClient, ConcurrentLinkedQueue<Message> messageQueue) {
        this.client=lobbyClient;
        this.messageQueue=messageQueue;
    }
    public void run() {
        Message inMessage;
        while (client.getSocket().isConnected()) {
            System.out.println("LISTENER ON");
            client.sendMessage(MsgType.INPUT,"","Lobby is listening");
            inMessage=client.getMessage();
            if (inMessage!=null) {
                messageQueue.add(inMessage);
                client.sendMessage(MsgType.INPUT,"","Added message to queue: "+((Info_Message)inMessage).getInfo());
                //TODO: addControllerResponse
            }
        }
        System.out.println("LISTENER OFF");
        //TODO: notify lobby that client disconnected
    }
}
