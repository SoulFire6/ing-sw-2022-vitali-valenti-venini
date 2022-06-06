package it.polimi.softeng.network.server;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.exceptions.GameIsOverException;
import it.polimi.softeng.exceptions.InvalidPlayerNumException;
import it.polimi.softeng.exceptions.LobbyClientDisconnectedException;
import it.polimi.softeng.exceptions.LobbyEmptyException;
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
            message=MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,"Lobby closed due to disconnection","Game over due to "+(lcde.getMessage().equals("")?"sudden disconnection":lcde.getMessage()+" has disconnected")+": "+controller.calculateWinningTeam()+" has won");
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
        clients.clear();
    }
    private void setupLobby(LobbyClient client) throws LobbyClientDisconnectedException {
        try {
            File saveDirectory=new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+"save");
            File[] saveList=saveDirectory.listFiles();
            client.sendMessage(MsgType.TEXT,"Lobby welcome message","Welcome to the lobby");
            while (whiteList==null) {
                client.sendMessage(MsgType.TEXT,"Create or load","[Create] or [Load] game:");
                switch(((Info_Message)client.getMessage()).getInfo().toUpperCase()) {
                    case "C":
                    case "CREATE":
                        whiteList=new ArrayList<>();
                        client.sendMessage(MsgType.TEXT,"New game","Creating new game");
                        while (maxPlayers<2 || maxPlayers>4) {
                            client.sendMessage(MsgType.TEXT,"Player num select","Player num(2-4):");
                            try {
                                maxPlayers=Integer.parseInt(((Info_Message)client.getMessage()).getInfo());
                            }
                            catch (NumberFormatException nfe) {
                                client.sendMessage(MsgType.TEXT,"Format error","Wrong format\nPlayer num(2-4):");
                            }
                        }
                        client.sendMessage(MsgType.TEXT,"Expert mode selection","Expert mode (y/n):");
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
                        saveFile=new File(saveDirectory.getPath()+lobbyName+"_"+maxPlayers+"_"+(expertMode?"expert":"normal"));
                        if (saveFile.exists()) {
                            client.sendMessage(MsgType.TEXT,"File exists","Existing file will be overwritten");
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
                        break;
                    case "L":
                    case "LOAD":
                        if (saveList==null || saveList.length==0) {
                            client.sendMessage(MsgType.TEXT,"No saves","No save files to load");
                            break;
                        }
                        for (int i=0; i<saveList.length; i++) {
                            client.sendMessage(MsgType.TEXT,"List files",i+": "+saveList[i].getName().replace(".bin",""));
                        }
                        int fileChoice=-1;
                        while (fileChoice<0 || fileChoice>saveList.length) {
                            client.sendMessage(MsgType.TEXT,"Select file","Choose file to load");
                            try {
                                fileChoice=Integer.parseInt(((Info_Message) client.getMessage()).getInfo());
                            }
                            catch (NumberFormatException nfe) {
                                client.sendMessage(MsgType.ERROR,"Incorrect format","Not a number");
                            }
                        }
                        controller=new LobbyController(lobbyName,saveList[fileChoice]);
                        if (controller.getGame()==null) {
                            client.sendMessage(MsgType.ERROR,"Game load error","Error loading game save");
                            saveFile.renameTo(new File(saveFile.getPath()+"_corrupted"));
                            this.whiteList=null;
                            break;
                        }
                        this.whiteList=new ArrayList<>();
                        for (Player p : controller.getGame().getPlayers()) {
                            this.whiteList.add(p.getName());
                        }
                        if (!whiteList.contains(lobbyMaster)) {
                            client.sendMessage(MsgType.DISCONNECT,"Not on whitelist","You are not a player on this save file "+this.whiteList);
                            this.whiteList=null;
                            break;
                        }
                        this.maxPlayers=controller.getGame().getPlayers().size();
                        this.expertMode=controller.getGame().isExpertMode();
                        break;
                    default:
                        client.sendMessage(MsgType.ERROR,"Out of bounds","Out of range (0-"+(saveList!=null?saveList.length-1:0)+")");
                        break;
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
            if (whiteList==null) {
                whiteList=new ArrayList<>();
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
            return this.lobbyName+": not ready";
        }
        return this.lobbyName+": "+(this.expertMode?"expert":"normal")+" game ["+this.clients.size()+"/"+this.maxPlayers+"]";
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
