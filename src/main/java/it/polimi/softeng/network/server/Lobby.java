package it.polimi.softeng.network.server;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;
import it.polimi.softeng.network.message.load.Game_Load_Msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Lobby implements Runnable {
    private final String lobbyName;
    //TODO: pass messages from queue to controller and add [maxPlayer] daemon thread to receive messages to put on the queue
    private final ConcurrentLinkedQueue<Message> lobbyMessageQueue=new ConcurrentLinkedQueue<>();
    private final HashMap<String,LobbyClient> clients=new HashMap<>();
    private final String lobbyMaster;
    private int maxPlayers=0;
    private LobbyController controller;

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
        currentClient.sendMessage(MsgType.TEXT,lobbyName,"Correct max player set","Set max players to "+maxPlayers);
        waitForOtherPlayers();
        //TODO add expertmode selection to lobby master
        setupGame(new ArrayList<>(this.clients.keySet()),false);
        createLobbyListeners();
        processMessageQueue();
    }

    private void setupLobby(LobbyClient client) {
        client.sendMessage(MsgType.TEXT,lobbyName,"Lobby welcome message","Welcome to the lobby");
        while (maxPlayers<2 || maxPlayers>4) {
            client.sendMessage(MsgType.INPUT,lobbyName,"Player num select","Player num(2-4):");
            try {
                maxPlayers=Integer.parseInt(((Info_Message)client.getMessage()).getInfo());
            }
            catch (NumberFormatException nfe) {
                if (nfe.getCause()==null) {
                    //TODO: delete lobby if lobby master disconnects before setup
                    System.out.println("Lobby master "+lobbyMaster+" disconnected, TODO: must delete lobby");
                } else {
                    client.sendMessage(MsgType.INPUT,lobbyName,"Format error","Wrong format\nPlayer num(2-4):");
                }
            }
        }
    }

    private void waitForOtherPlayers() {
        try {
            synchronized (clients) {
                while (clients.size()<maxPlayers) {
                    for (String clientName: clients.keySet()) {
                        clients.get(clientName).sendMessage(MsgType.TEXT,lobbyName,"Connect to lobby msg",maxPlayers+" player game, current num: ["+ clients.size()+"/"+maxPlayers+"]");
                    }
                    clients.wait();
                }
                System.out.println("GOT MAX CLIENTS CONNECTED: ");
                for (String clientName: clients.keySet()) {
                    clients.get(clientName).sendMessage(MsgType.TEXT,lobbyName,"Lobby full","Lobby now full ["+ clients.size()+"/"+maxPlayers+"]");
                    System.out.println(clientName);
                }
            }
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    private void setupGame(ArrayList<String> playerNames, boolean expertMode) {
        this.controller=new LobbyController(playerNames,expertMode,lobbyName);
        Message gameLoad=new Game_Load_Msg(lobbyName,"Game created",controller.getGame());
        for (String client: clients.keySet()) {
            clients.get(client).sendMessage(gameLoad);
        }
    }
    public HashMap<String,LobbyClient> getClients() {
        return this.clients;
    }
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    public void createLobbyListeners() {
        Thread listener;
        for (String client: clients.keySet()) {
            listener=new Thread(new LobbyListener(clients.get(client),lobbyName,lobbyMessageQueue));
            listener.setDaemon(true);
            listener.start();
        }
    }
    public void processMessageQueue() {
        Message msg;
        while (clients.size()!=0) {
            checkClientConnection();
            synchronized (lobbyMessageQueue) {
                while(clients.size()>0) {
                    while (lobbyMessageQueue.size()>0) {
                        msg=lobbyMessageQueue.poll();
                        //Check print
                        System.out.println("["+lobbyName+"]: processed message ("+((Info_Message)msg).getInfo()+"), queue size: "+lobbyMessageQueue.size());
                        //controller.processMessage(msg,clients.get(msg.getSender()));
                    }
                }
            }
        }
    }
    public void checkClientConnection() {
        for (String client: clients.keySet()) {
            if (!clients.get(client).getSocket().isConnected()) {
                clients.remove(client);
            }
        }
    }
}
