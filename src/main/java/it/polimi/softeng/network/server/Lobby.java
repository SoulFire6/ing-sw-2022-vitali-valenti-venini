package it.polimi.softeng.network.server;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Lobby implements Runnable {
    private final String lobbyName;
    private final ConcurrentLinkedQueue<Message> lobbyMessageQueue=new ConcurrentLinkedQueue<>();
    private final HashMap<String,LobbyClient> clients=new HashMap<>();

    private final HashMap<String,LobbyListener> listeners=new HashMap<>();
    private final String lobbyMaster;
    private int maxPlayers=0;
    private Boolean expertMode=null;
    private LobbyController controller=null;

    private ArrayList<String> whiteList=null;

    private File saveFile=null;

    public Lobby(String lobbyName,String username, LobbyClient lobbyMaster) {
        this.lobbyName=lobbyName;
        this.clients.put(username,lobbyMaster);
        this.lobbyMaster=username;
    }

    @Override
    public void run() {
        System.out.println("LOBBY CREATED: " + lobbyName);
        Message message=null;
        try {
            setupLobby(clients.get(lobbyMaster));
            System.out.println("Creating listener for lobbymaster: "+lobbyMaster);
            listeners.put(lobbyMaster,new LobbyListener(clients.get(lobbyMaster),lobbyName,maxPlayers,lobbyMessageQueue,clients,listeners));
            waitForOtherPlayers();
            setupGame();
            processMessageQueue();
        }
        catch (LobbyEmptyException lee) {
            System.out.println("Lobby "+lobbyName+" is empty");
        }
        catch (LobbyClientDisconnectedException lcde) {
            message=MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,"Lobby closed due to disconnection",(lcde.getMessage().equals("")?"sudden disconnection":lcde.getMessage()+" has disconnected")+": save file to continue game "+saveFile.getName());
        }
        catch (GameIsOverException gioe) {
            String winningTeam="no team";
            if (controller!=null) {
                winningTeam=controller.calculateWinningTeam().toString();
            }
            message=MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,"Game over: "+winningTeam,"Game over: "+winningTeam+" has won");
        }
        catch (InvalidPlayerNumException ipne) {
            System.out.println("Invalid player num, closing lobby");
        }
        if (message!=null) {
            System.out.println(message.getContext());
            for (String client : clients.keySet()) {
                try {
                    clients.get(client).sendMessage(message);
                }
                catch (LobbyClientDisconnectedException ignored) {
                }
            }
        }
        if (controller!=null) {
            controller.closeFileStream();
        }
        clients.clear();
    }
    private void setupLobby(LobbyClient client) throws LobbyClientDisconnectedException {
        try {
            File saveDirectory=new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+"save");
            File[] saveList=saveDirectory.listFiles(file -> file.getName().endsWith(".bin"));
            if (saveList==null) {
                saveList=new File[]{};
            }
            int fileChoice=-1;
            client.sendMessage(MsgType.TEXT,"Lobby welcome message","Welcome to the lobby");
            while(whiteList==null) {
                client.sendMessage(MsgType.TEXT,"Save select","Select a save file or create a new game");
                StringBuilder saveListOptions=new StringBuilder();
                saveListOptions.append("[0 - Create new game]\n");
                for (int i=0; i<saveList.length; i++) {
                    saveListOptions.append("[").append(i + 1).append(" - ").append(saveList[i].getName().replace(".bin", "")).append("]\n");
                }
                client.sendMessage(MsgType.INPUT,"Select save file",saveListOptions.toString());
                while (fileChoice<0 || fileChoice>saveList.length) {
                    try {
                        fileChoice=Integer.parseInt(((Info_Message) client.getMessage()).getInfo());
                    }
                    catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        client.sendMessage(MsgType.ERROR,"Incorrect format","Not a number : "+((Info_Message) client.getMessage()).getInfo());
                    }
                }
                if (fileChoice==0) {
                    client.sendMessage(MsgType.TEXT,"New game","Creating new game");
                    while (maxPlayers<2 || maxPlayers>4) {
                        client.sendMessage(MsgType.INPUT,"Player num select","Player num [2][3][4]:");
                        try {
                            maxPlayers=Integer.parseInt(((Info_Message)client.getMessage()).getInfo());
                        }
                        catch (NumberFormatException nfe) {
                            client.sendMessage(MsgType.ERROR,"Format error","Wrong format\nPlayer num(2-4):");
                        }
                    }
                    client.sendMessage(MsgType.INPUT,"Expert mode selection","Expert mode [y]/[n]):");
                    while (expertMode==null) {
                        switch(((Info_Message)client.getMessage()).getInfo().toUpperCase()) {
                            case "Y":
                                expertMode=true;
                                break;
                            case "N":
                                expertMode=false;
                                break;
                            default:
                                client.sendMessage(MsgType.TEXT,"Format error","Wrong format\nExpert mode (y/n):");
                                break;
                        }
                    }
                    saveFile=new File(saveDirectory.getPath()+"/"+lobbyName.replace(" ","_")+"_"+maxPlayers+"_"+(expertMode?"expert":"normal")+".bin");
                    if (saveFile.exists()) {
                        client.sendMessage(MsgType.TEXT,"File exists","Existing file with same name will be overwritten");
                    } else {
                        try {
                            if (!saveFile.createNewFile()) {
                                throw new IOException();
                            }
                        }
                        catch (IOException io) {
                            client.sendMessage(MsgType.ERROR,"File system error: could not create save file","Error creating save file");
                        }
                    }
                    this.whiteList=new ArrayList<>();
                } else {
                    saveFile=saveList[fileChoice-1];
                    try {
                        controller=new LobbyController(lobbyName,saveFile);
                        whiteList=new ArrayList<>();
                        for (Player p : controller.getGame().getPlayers()) {
                            whiteList.add(p.getName());
                        }
                        if (!whiteList.contains(lobbyMaster)) {
                            client.sendMessage(MsgType.DISCONNECT,"Not on whitelist","You are not a player on this save file "+this.whiteList);
                            this.whiteList=null;
                            break;
                        }
                        maxPlayers=controller.getGame().getPlayers().size();
                        expertMode=controller.getGame().isExpertMode();
                    }
                    catch (GameLoadException gle) {
                        client.sendMessage(MsgType.ERROR,"Game load error","Error loading game save");
                        saveFile.renameTo(new File(saveFile.getPath()+"_corrupted"));
                        fileChoice=-1;
                    }
                }
            }
            client.sendMessage(MsgType.TEXT, "Game setup params", "Game parameters\nPlayer num: " + maxPlayers + "\nExpert mode: " + expertMode);
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
            throw new LobbyClientDisconnectedException("Lobby master "+lobbyMaster);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void waitForOtherPlayers() throws LobbyEmptyException,GameIsOverException {
        String newPlayer;
        synchronized (clients) {
            while (clients.size() < maxPlayers && clients.size()>0) {
                newPlayer=null;
                try {
                    clients.wait();
                    for (String clientName : clients.keySet()) {
                        if (listeners.get(clientName)==null) {
                            System.out.println("Creating listener for: "+clientName);
                            listeners.put(clientName,new LobbyListener(clients.get(clientName),lobbyName,maxPlayers,lobbyMessageQueue,clients,listeners));
                            clients.get(clientName).sendMessage(MsgType.TEXT, "Connect to lobby msg", maxPlayers + " player " + (expertMode ? "expert" : "normal") + " game");
                            newPlayer=clientName;
                        }
                    }
                    if (newPlayer!=null) {
                        sendToAll(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Connect",newPlayer+" has joined, current players: [" + clients.size() + "/" + maxPlayers + "]"));
                    }
                } catch (LobbyClientDisconnectedException lcde) {
                    if (clients.size()==0) {
                        throw new LobbyEmptyException("Lobby is empty");
                    }
                }
                catch (InterruptedException ie) {
                    System.out.println("Lobby " + lobbyName + " interrupted whilst waiting for connections");
                }
                if (clients.size()==maxPlayers) {
                    System.out.println("[" + lobbyName + "] GOT MAX CLIENTS CONNECTED: " + clients.keySet());
                    try {
                        sendToAll(MessageCenter.genMessage(MsgType.TEXT, lobbyName, "Lobby full", "Lobby now full [" + clients.size() + "/" + maxPlayers + "], setting up game..."));
                    }
                    catch (LobbyClientDisconnectedException lcde) {
                        throw new GameIsOverException(lcde.getMessage() + " disconnected before game could be setup");
                    }
                }
            }
            if (whiteList.size()==0) {
                whiteList.addAll(clients.keySet());
            }
        }
    }
    private void sendToAll(Message msg) throws LobbyClientDisconnectedException {
        boolean clientDisconnected=false;
        synchronized (clients) {
            for (String clientName : clients.keySet()) {
                try {
                    clients.get(clientName).sendMessage(msg);
                }
                catch (LobbyClientDisconnectedException lcde) {
                    clients.remove(clientName);
                    clientDisconnected=true;
                    sendToAll(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Client disconnect",clientName+" has disconnected, current players: ["+ clients.size()+"/"+maxPlayers+"]"));
                }
            }
        }
        if (clientDisconnected) {
            throw new LobbyClientDisconnectedException("");
        }
    }
    private void setupGame() throws InvalidPlayerNumException,LobbyClientDisconnectedException {
        if (clients.size()==maxPlayers) {
            if (controller==null) {
                this.controller=new LobbyController(new ArrayList<>(clients.keySet()),expertMode,lobbyName,saveFile);
            }
            System.out.println("GAME SETUP");
            Message gameLoad=MessageCenter.genMessage(MsgType.GAME,lobbyName,"Game has been setup",new ReducedGame(controller.getGame(),controller.getTurnManager()));
            for (String client: clients.keySet()) {
                clients.get(client).sendMessage(gameLoad);
            }
        } else {
            throw new InvalidPlayerNumException(clients.size()+" does not equal "+maxPlayers);
        }
    }
    public HashMap<String,LobbyClient> getClients() {
        return this.clients;
    }
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    public ArrayList<String> getWhiteList() {
        return this.whiteList;
    }
    public String getLobbyStats() {
        if (maxPlayers==0) {
            return "["+this.lobbyName+"]: not ready";
        }
        ArrayList<String> waitingFor = new ArrayList<>(whiteList);
        waitingFor.removeAll(clients.keySet());
        return "["+this.lobbyName+"]: "+(this.expertMode?"expert":"normal")+" game ["+this.clients.size()+"/"+this.maxPlayers+"], Currently connected: "+this.clients.keySet()+(waitingFor.size()>0?", Waiting for: "+waitingFor:"");
    }
    public void processMessageQueue() throws LobbyClientDisconnectedException,GameIsOverException,LobbyEmptyException {
        Message msg;
        synchronized (lobbyMessageQueue) {
            while(clients.size()==maxPlayers) {
                while (lobbyMessageQueue.size()>0) {
                    msg=lobbyMessageQueue.poll();
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
                    System.out.println("["+lobbyName+"]: processed message ("+msg.getSender()+": "+msg.getClass().getSimpleName()+"), queue size: "+lobbyMessageQueue.size());
                }
            }
            throw new LobbyClientDisconnectedException("");
        }
    }
}
