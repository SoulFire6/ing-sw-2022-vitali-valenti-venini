package it.polimi.softeng.network.server;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;
import it.polimi.softeng.network.message.load.Game_Load_Msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Lobby implements Runnable {
    private final String lobbyName;
    private final ConcurrentLinkedQueue<Message> lobbyMessageQueue=new ConcurrentLinkedQueue<>();
    private final HashMap<String,LobbyClient> clients=new HashMap<>();
    private final String lobbyMaster;
    private int maxPlayers=0;
    private Boolean expertMode=null;
    private LobbyController controller=null;

    public Lobby(String lobbyName,String username, LobbyClient lobbyMaster) {
        this.lobbyName=lobbyName;
        this.clients.put(username,lobbyMaster);
        this.lobbyMaster=username;
    }

    @Override
    public void run() {
        System.out.println("LOBBY CREATED: "+lobbyName);
        setupLobby(clients.get(lobbyMaster));
        clients.get(lobbyMaster).sendMessage(MsgType.TEXT,"Game setup params","Game parameters\nPlayer num: "+maxPlayers+"\nExpert mode: "+expertMode);
        waitForOtherPlayers();
        //TODO add expertmode selection to lobby master
        try {
            setupGame(new ArrayList<>(this.clients.keySet()),expertMode);
            createLobbyListeners();
            processMessageQueue();
        }
        catch (InvalidPlayerNumException ipne) {
            System.out.println("Invalid player num, closing lobby");
        }
    }
    private void setupLobby(LobbyClient client) {
        client.sendMessage(MsgType.TEXT,"Lobby welcome message","Welcome to the lobby");
        while (maxPlayers<2 || maxPlayers>4) {
            client.sendMessage(MsgType.INPUT,"Player num select","Player num(2-4):");
            try {
                maxPlayers=Integer.parseInt(((Info_Message)client.getMessage()).getInfo());
            }
            catch (NumberFormatException nfe) {
                if (nfe.getCause()==null) {
                    //TODO: delete lobby if lobby master disconnects before setup
                    System.out.println("Lobby master "+lobbyMaster+" disconnected, TODO: must delete lobby");
                } else {
                    client.sendMessage(MsgType.INPUT,"Format error","Wrong format\nPlayer num(2-4):");
                }
            }
        }
        client.sendMessage(MsgType.INPUT,"Expert mode selection","Expert mode (y/n):");
        while (expertMode==null) {
            switch(((Info_Message)client.getMessage()).getInfo().toUpperCase()) {
                case "Y":
                case "T":
                case "TRUE":
                    expertMode=true;
                    break;
                case "N":
                case "F":
                case "FALSE":
                    expertMode=false;
                    break;
                default:
                    client.sendMessage(MsgType.INPUT,"Format error","Wrong format\nExpert mode (y/n):");
                    break;
            }
        }
    }
    private void waitForOtherPlayers() {
        try {
            synchronized (clients) {
                while (clients.size()<maxPlayers) {

                    for (String clientName: clients.keySet()) {
                        clients.get(clientName).sendMessage(MsgType.TEXT,"Connect to lobby msg",maxPlayers+" player "+(expertMode?"expert":"normal")+" game, current players: ["+ clients.size()+"/"+maxPlayers+"]");
                    }
                    clients.wait();
                }
                System.out.println("["+lobbyName+"] GOT MAX CLIENTS CONNECTED: "+clients.keySet());
                for (String clientName: clients.keySet()) {
                    clients.get(clientName).sendMessage(MsgType.TEXT,"Lobby full"," Lobby now full ["+ clients.size()+"/"+maxPlayers+"], setting up game...");
                }
            }
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    private void setupGame(ArrayList<String> playerNames, boolean expertMode) throws InvalidPlayerNumException {
        this.controller=new LobbyController(playerNames,expertMode,lobbyName);
        Message gameLoad=new Game_Load_Msg(lobbyName,"Game has been setup",controller.getGame());
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
    public String getLobbyStats() {
        if (maxPlayers==0) {
            return this.lobbyName+": not ready";
        }
        return this.lobbyName+": "+(this.expertMode?"expert":"normal")+" game ["+this.clients.size()+"/"+this.maxPlayers+"]";
    }
    public void createLobbyListeners() {
        Thread listener;
        for (String client: clients.keySet()) {
            listener=new Thread(new LobbyListener(clients.get(client),lobbyMessageQueue));
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
                        if (msg.getSubType()==MsgType.CLOSE) {
                            System.out.println(msg.getContext());
                            return;
                        }
                        for (Message message : controller.parseMessage(msg)) {
                            switch (message.getSubType()) {
                                case WHISPER:
                                    clients.get(message.getContext()).sendMessage(message);
                                    break;
                                case ERROR:
                                    clients.get(msg.getSender()).sendMessage(message);
                                    break;
                                default:
                                    for (String client : clients.keySet()) {
                                        clients.get(client).sendMessage(message);
                                    }
                                    break;
                            }
                        }
                        //Check print
                        System.out.println("["+lobbyName+"]: processed message ("+msg.getSender()+": "+msg.getClass()+"), queue size: "+lobbyMessageQueue.size());
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
