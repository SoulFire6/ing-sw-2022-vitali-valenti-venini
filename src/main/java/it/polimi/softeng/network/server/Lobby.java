package it.polimi.softeng.network.server;

import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Lobby implements Runnable {
    private final String lobbyName;
    //TODO: pass messages from queue to controller and add [maxPlayer] daemon thread to receive messages to put on the queue
    private final ConcurrentLinkedQueue<Message> lobbyMessageQueue=new ConcurrentLinkedQueue<>();
    private final HashMap<String,LobbyClient> clients=new HashMap<>();
    private String lobbyMaster;
    private int maxPlayers=0;

    public Lobby(String lobbyName,String username, LobbyClient lobbyMaster) {
        this.lobbyName=lobbyName;
        this.clients.put(username,lobbyMaster);
        this.lobbyMaster=username;
    }

    @Override
    public void run() {
        System.out.println("LOBBY CREATED: "+lobbyName);
        setupLobby(clients.get(lobbyMaster));
        LobbyClient currentClient=clients.get(lobbyMaster);
        currentClient.printOut(MsgType.TEXT,lobbyName,"Correct max player set","Set max players to "+maxPlayers);
        waitForOtherPlayers();
    }

    private void setupLobby(LobbyClient client) {
        client.printOut(MsgType.TEXT,lobbyName,"Lobby welcome message","Welcome to the lobby");
        while (maxPlayers<2 || maxPlayers>4) {
            client.printOut(MsgType.INPUT,lobbyName,"Player num select","Player num(2-4):");
            try {
                maxPlayers=Integer.parseInt(((Info_Message)client.getIn()).getInfo());
            }
            catch (NumberFormatException | IOException e) {
                if (e.getCause()==null) {
                    //TODO: delete lobby if lobby master disconnects before setup
                    System.out.println("Lobby master "+lobbyMaster+" disconnected, TODO: must delete lobby");
                } else {
                    client.printOut(MsgType.INPUT,lobbyName,"Format error","Wrong format\nPlayer num(2-4):");
                }
            }
        }

    }

    private void waitForOtherPlayers() {
        try {
            synchronized (clients) {
                while (clients.size()<maxPlayers) {
                    for (String clientName: clients.keySet()) {
                        clients.get(clientName).printOut(MsgType.TEXT,lobbyName,"Connect to lobby msg",maxPlayers+" player game, current num: ["+ clients.size()+"/"+maxPlayers+"]");
                    }
                    clients.wait();
                }
                System.out.println("GOT MAX CLIENTS CONNECTED: ");
                for (String clientName: clients.keySet()) {
                    clients.get(clientName).printOut(MsgType.TEXT,lobbyName,"Lobby full","Lobby now full ["+ clients.size()+"/"+maxPlayers+"]");
                    System.out.println(clientName);
                }
            }
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    public HashMap<String,LobbyClient> getClients() {
        return this.clients;
    }
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
}
