package it.polimi.softeng.network.server;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.Player;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.model.ReducedModel.ReducedTurnState;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the Lobby class. It manages the lobby from its creation to the game setup, when the maximum number of player is reached.
 */

public class Lobby implements Runnable {
    private final String lobbyName;
    private final ConcurrentLinkedQueue<Message> lobbyMessageQueue=new ConcurrentLinkedQueue<>();
    private final HashMap<String,LobbyClient> clients=new HashMap<>();

    private final HashMap<String,LobbyListener> listeners=new HashMap<>();
    private String lobbyMaster;
    private int maxPlayers=0;
    private Boolean expertMode=null;
    private LobbyController controller=null;

    private ArrayList<String> whiteList=null;

    private File saveFile=null;

    private boolean gameStarted=false;

    /**
     * @param lobbyName String which identifies the lobby
     * @param username String name of the first player who creates this lobby
     * @param lobbyMaster the first player, who created the lobby
     */
    public Lobby(String lobbyName,String username, LobbyClient lobbyMaster) {
        this.lobbyName=lobbyName;
        this.clients.put(username,lobbyMaster);
        this.lobbyMaster=username;
    }

    /**
     * Run method of the Lobby. Once created, the lobby waits for other players.
     * When the number of players is reached, it invokes the setupGame() method, then manages the message queue.
     */

    @Override
    public void run() {
        System.out.println("LOBBY CREATED: " + lobbyName);
        try {
            setupLobby(clients.get(lobbyMaster));
            System.out.println("Creating listener for lobbymaster: "+lobbyMaster);
            listeners.put(lobbyMaster,new LobbyListener(clients.get(lobbyMaster),lobbyName,maxPlayers,lobbyMessageQueue,clients,listeners));
            checkClientsThread();
            waitForOtherPlayers();
            setupGame();
            processMessageQueue();
        }
        catch (LobbyEmptyException lee) {
            System.out.println("Lobby "+lobbyName+" is empty");
        }
        catch (LobbyClientDisconnectedException lcde) {
            sendToAll(MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,"Lobby closed due to disconnection",lcde.getMessage()));
        }
        catch (GameIsOverException gioe) {
            String winningTeam="no team";
            if (controller!=null) {
                winningTeam=controller.calculateWinningTeam(clients.keySet()).toString();
            }
            sendToAll(MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,"Game over: "+winningTeam,"Game over: "+winningTeam+" has won"));
            saveFile.delete();
        }
        catch (InvalidPlayerNumException ipne) {
            System.out.println("Invalid player num, closing lobby");
        }
        finally {
            if (controller!=null) {
                controller.closeFileStream();
            }
            clients.clear();
        }
    }

    /**
     * @param client the first client, who created the lobby
     * @throws LobbyClientDisconnectedException when a client does disconnect from the lobby
     */
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
                StringBuilder saveListOptions=new StringBuilder();
                saveListOptions.append("0 > Create new game-");
                for (int i=0; i<saveList.length; i++) {
                    saveListOptions.append(i + 1).append(" > ").append(saveList[i].getName().replace(".bin", "")).append("-");
                }
                while (fileChoice<0 || fileChoice>saveList.length) {
                    client.sendMessage(MsgType.INPUT,saveListOptions.substring(0,saveListOptions.length()-1),"Select save file or create new game");
                    try {
                        fileChoice=Integer.parseInt(((Info_Message) client.getMessage()).getInfo());
                    }
                    catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        client.sendMessage(MsgType.ERROR,"Incorrect format","Not a number : "+((Info_Message) client.getMessage()).getInfo());
                    }
                    catch (NullPointerException npe) {
                        throw new LobbyClientDisconnectedException("Lobby master");
                    }
                }
                if (fileChoice==0) {
                    client.sendMessage(MsgType.TEXT,"New game","Creating new game");
                    while (maxPlayers<2 || maxPlayers>4) {
                        client.sendMessage(MsgType.INPUT,"2 > Black vs White-3 > Black vs White vs Grey-4 > 2 Black vs 2 White","Player num (2-4):");
                        try {
                            maxPlayers=Integer.parseInt(((Info_Message)client.getMessage()).getInfo());
                        }
                        catch (NumberFormatException nfe) {
                            client.sendMessage(MsgType.ERROR,"Format error","Wrong format\nPlayer num(2-4):");
                        }
                    }
                    client.sendMessage(MsgType.INPUT,"Y > Expert-N > Normal","Expert mode (y/n):");
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
                            saveList=saveDirectory.listFiles(file -> file.getName().endsWith(".bin"));
                        }
                    }
                    whiteList=new ArrayList<>();
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
                        saveFile.delete();
                        fileChoice=-1;
                    }
                }
            }
            client.sendMessage(MsgType.CLIENT_NUM, "["+client.getUsername()+"]","Game parameters\nPlayer num: " + maxPlayers + "\nExpert mode: " + expertMode);
        }
        catch (NullPointerException npe) {
            //Delete incomplete save file
            if (saveFile!=null) {
                saveFile.delete();
            }
            npe.printStackTrace();
            throw new LobbyClientDisconnectedException("Lobby master "+lobbyMaster);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method waits for clients to connect to the lobby.
     * This method runs until the required number of clients connect to the lobby.
     * @throws LobbyEmptyException if the lobby remains empty
     * @throws GameIsOverException if the game finishes before the setup
     */
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
                        sendToAll(MessageCenter.genMessage(MsgType.CLIENT_NUM,lobbyName,clients.keySet().toString(),newPlayer+" has joined, current players: [" + clients.size() + "/" + maxPlayers + "]"));
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
                    sendToAll(MessageCenter.genMessage(MsgType.TEXT, lobbyName, "Lobby full", "Lobby now full [" + clients.size() + "/" + maxPlayers + "], setting up game..."));
                    if (clients.size()<maxPlayers) {
                        throw new GameIsOverException("A client disconnected before game could be setup");
                    }
                }
            }
            if (whiteList==null) {
                whiteList=new ArrayList<>();
            }
            if (whiteList.size()==0) {
                whiteList.addAll(clients.keySet());
            }
        }
    }

    /**
     * Method that checks if the lobby master disconnected. If so, the lobby master is reassigned randomly and the other clients are notified.
     * Also, it skips turns of disconnected players
     */
    private void checkClientsThread() {
        Thread thread=new Thread(()->{
            synchronized (clients) {
                while (clients.size()>0) {
                    //Check if lobby master is still connected
                    if (!clients.containsKey(lobbyMaster)) {
                        Random rand=new Random();
                        Object[] values=clients.keySet().toArray();
                        lobbyMaster=(String)values[rand.nextInt(values.length)];
                        sendToAll(MessageCenter.genMessage(MsgType.CLIENT_NUM,lobbyName,"Lobby master disconnected","Lobby master is now "+lobbyMaster));
                    }
                    //Skip disconnected players
                    if (controller!=null && controller.getTurnManager()!=null) {
                        if (!clients.containsKey(controller.getTurnManager().getCurrentPlayer().getName())) {
                            Player skippedPlayer=controller.getTurnManager().getCurrentPlayer();
                            controller.skipPlayerTurn(skippedPlayer);
                            sendToAll(MessageCenter.genMessage(MsgType.TURNSTATE,"SERVER",skippedPlayer.getName()+" is not connected, skipping turn",new ReducedTurnState(controller.getTurnManager())));
                        }
                    }
                    try {
                        clients.wait();
                    }
                    catch (InterruptedException ignored) {
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Method used to send a message to every client
     * @param msg Message to be sent to every client
     */
    private void sendToAll(Message msg) {
        synchronized (clients) {
            for (String clientName : clients.keySet()) {
                try {
                    clients.get(clientName).sendMessage(msg);
                }
                catch (LobbyClientDisconnectedException lcde) {
                    clients.remove(clientName);
                    sendToAll(MessageCenter.genMessage(MsgType.CLIENT_NUM,lobbyName,clients.keySet().toString(),clientName+" has disconnected, current players: ["+ clients.size()+"/"+maxPlayers+"]"));
                }
            }
        }
    }

    /**
     * This method does the game setup
     * @throws InvalidPlayerNumException if the game is being set up without the number of clients being equal to the game players number
     * @throws LobbyClientDisconnectedException if a client disconnects from the lobby
     */
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

    /**
     * @return HashMap<String,LobbyClient> representing the clients of the lobby
     */
    public HashMap<String,LobbyClient> getClients() {
        return this.clients;
    }

    /**
     * @return int number of players that can connect to this lobby
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * @return ArrayList of usernames in lobby whitelist (usernames that are allowed to connect to this lobby)
     */

    public ArrayList<String> getWhiteList() {
        return this.whiteList;
    }


    /**
     * @return String explaining the status of the lobby
     */
    public String getLobbyStats() {
        if (maxPlayers==0) {
            return this.lobbyName+ " > "+this.lobbyName+" is not ready";
        }
        ArrayList<String> waitingFor = new ArrayList<>(whiteList);
        waitingFor.removeAll(clients.keySet());
        return this.lobbyName+" > "+(this.expertMode?"expert":"normal")+" game ["+this.clients.size()+"/"+this.maxPlayers+"]\n Currently connected: "+this.clients.keySet()+(waitingFor.size()>0?"\nWaiting for: "+waitingFor:"");
    }

    /**
     * Method that processes and manages the messages present in the queue
     * @throws LobbyClientDisconnectedException when a client disconnects from the lobby
     * @throws GameIsOverException when the game ends
     * @throws LobbyEmptyException when the lobby remains empty
     */
    public void processMessageQueue() throws LobbyClientDisconnectedException,GameIsOverException,LobbyEmptyException {
        gameStarted=true;
        Message msg;
        synchronized (lobbyMessageQueue) {
            while(controller.checkConnectedTeams(clients.keySet())) {
                while (lobbyMessageQueue.size()>0) {
                    msg=lobbyMessageQueue.poll();
                    if (msg.getSubType().equals(MsgType.SAVE_AND_QUIT)) {
                        if (msg.getSender().equals(lobbyMaster)) {
                            throw new LobbyClientDisconnectedException("Lobby master "+lobbyMaster+" has decided to save and quit the game, the save file to continue is: "+saveFile.getName().replace(".bin",""));
                        } else {
                            clients.get(msg.getSender()).sendMessage(MsgType.ERROR,"Not lobby master","Only the current lobby master ("+lobbyMaster+") can decide to save and quit the game");
                        }
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
                    System.out.println("["+lobbyName+"]: processed message ("+msg.getSender()+": "+msg.getClass().getSimpleName()+"), queue size: "+lobbyMessageQueue.size());
                }
            }
            throw new GameIsOverException("Not enough players to continue game");
        }
    }

    /**
     * This method adds a client to the lobby and loads the game if it has already started (in case of reconnect)
     * @param client the client to add to the lobby
     */

    public void connectClient(LobbyClient client) {
        synchronized (clients) {
            clients.put(client.getUsername(), client);
            //Sends game to client if they have rejoined
            if (gameStarted) {
                try {
                    client.sendMessage(MsgType.GAME,"Rejoined game",new ReducedGame(controller.getGame(),controller.getTurnManager()));
                }
                catch (LobbyClientDisconnectedException lcde) {
                    clients.remove(client.getUsername());
                }
            }
           clients.notifyAll();
        }
    }
}
