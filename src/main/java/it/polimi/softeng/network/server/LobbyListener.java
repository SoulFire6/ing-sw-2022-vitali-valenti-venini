package it.polimi.softeng.network.server;

import it.polimi.softeng.network.message.Message;

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
        while (client.getSocket().isConnected() && (inMessage=client.getMessage())!=null) {
            System.out.println("LISTENER ON");
            messageQueue.add(inMessage);
        }
        System.out.println("LISTENER OFF");
        //TODO: notify lobby that client disconnected
    }
}
