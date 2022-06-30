package it.polimi.softeng.network.server;

import it.polimi.softeng.exceptions.LobbyClientDisconnectedException;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *  This class defines the listener
 */
public class LobbyListener implements Runnable {
    private final LobbyClient client;
    private final String lobbyName;
    private final int maxPlayers;
    private final ConcurrentLinkedQueue<Message> messageQueue;
    private final HashMap<String,LobbyClient> clients;
    private final HashMap<String,LobbyListener> listeners;

    /**
     * @param lobbyClient the LobbyClient, representing one client over the lobby
     * @param lobbyName the identifier of the lobby
     * @param maxPlayers max number of players the lobby can hold
     * @param messageQueue LinkedQueue that will hold the messages
     * @param clients HashMap<String,LobbyClient> representing the clients of the lobby
     * @param listeners HashMap<String,LobbyListener> representing the listeners over the lobby
     */
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

    /**
     * The listener waits for messages as long as the client remains connected to the lobby.
     * When the client disconnects, the listener for this client gets removed.
     */
    public void run() {
        Message inMessage;
        while (client.getSocket().isConnected() && (inMessage=client.getMessage())!=null) {
            messageQueue.add(inMessage);
        }
        synchronized (listeners) {
            System.out.println("Removing listener for "+client.getUsername());
            listeners.remove(client.getUsername());
        }
        synchronized (clients) {
            clients.remove(client.getUsername());
            Message message=MessageCenter.genMessage(MsgType.CLIENT_NUM,lobbyName,clients.keySet().toString(), client.getUsername()+" has disconnected, current players: [" + clients.size() + "/" + maxPlayers + "]");
            for (String clientName : clients.keySet()) {
                try {
                    clients.get(clientName).sendMessage(message);
                }
                catch (LobbyClientDisconnectedException ignored) {
                }
            }
            clients.notifyAll();
        }
    }
}
