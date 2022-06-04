package it.polimi.softeng.network.server;

import it.polimi.softeng.exceptions.LobbyClientDisconnectedException;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LobbyListener implements Runnable {
    private final LobbyClient client;
    private final String lobbyName;
    private final int maxPlayers;
    private final ConcurrentLinkedQueue<Message> messageQueue;
    private final HashMap<String,LobbyClient> clients;
    private final HashMap<String,LobbyListener> listeners;
    LobbyListener(LobbyClient lobbyClient, String lobbyName, int maxPlayers, ConcurrentLinkedQueue<Message> messageQueue, HashMap<String,LobbyClient> clients, HashMap<String,LobbyListener> listeners) {
        this.client=lobbyClient;
        this.lobbyName=lobbyName;
        this.maxPlayers=maxPlayers;
        this.messageQueue=messageQueue;
        this.clients=clients;
        this.listeners=listeners;
        Thread listener=new Thread(this);
        listener.setDaemon(true);
        listener.start();
    }
    public void run() {
        Message inMessage;
        while (client.getSocket().isConnected() && (inMessage=client.getMessage())!=null) {
            System.out.println("LISTENER ON");
            messageQueue.add(inMessage);
        }
        synchronized (listeners) {
            System.out.println("Removing listener for "+client.getUsername());
            listeners.remove(client.getUsername());
        }
        synchronized (clients) {
            clients.remove(client.getUsername());
            Message message=MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Disconnect", client.getUsername()+" has disconnected, current players: [" + clients.size() + "/" + maxPlayers + "]");
            for (String clientName : clients.keySet()) {
                try {
                    clients.get(clientName).sendMessage(message);
                }
                catch (LobbyClientDisconnectedException ignored) {
                }

            }
            clients.notify();
        }
    }
}
