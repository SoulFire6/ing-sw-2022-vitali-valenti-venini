package it.polimi.softeng.network.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Lobby implements Runnable {
    private final String lobbyName;
    private final ConcurrentLinkedQueue<String> lobbyMessageQueue=new ConcurrentLinkedQueue<>();
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

        currentClient.printOut("Set max players to "+maxPlayers);
        waitForOtherPlayers();
    }

    private void setupLobby(LobbyClient client) {
        client.printOut("["+lobbyName+"] Welcome to the lobby");
        while (maxPlayers<2 || maxPlayers>4) {
            client.printOut("Player num(2-4): \nDONE");
            try {
                maxPlayers=Integer.parseInt(client.getIn());
            }
            catch (NumberFormatException | IOException e) {
                if (e.getCause()==null) {
                    //TODO: delete lobby if lobby master disconnects before setup
                    System.out.println("Lobby master "+lobbyMaster+" disconnected, TODO: must delete lobby");
                } else {
                    client.printOut("Wrong format\nPlayer num(2-4): \nDONE");
                }
            }
        }

    }

    private void waitForOtherPlayers() {
        try {
            synchronized (clients) {
                while (clients.size()<maxPlayers) {
                    for (String clientName: clients.keySet()) {
                        clients.get(clientName).printOut(maxPlayers+" player server, current num: "+ clients.size());
                    }
                    clients.wait();
                }
                System.out.println("GOT MAX CLIENTS CONNECTED");
                for (String username: clients.keySet()) {
                    System.out.println(username);
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
