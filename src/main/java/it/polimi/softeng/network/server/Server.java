package it.polimi.softeng.network.server;

import it.polimi.softeng.exceptions.ServerCreationException;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;

public class Server {
    private static final HashMap<String,Lobby> lobbies=new HashMap<>();
    private static final Integer SERVER_PORT=50033;
    public void main(String[] args) throws ServerCreationException {
        try {
            File saveDirectory=new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+"save");
            if (!saveDirectory.exists()) {
                if (!saveDirectory.mkdir()) {
                    throw new ServerCreationException("Error creating save file directory");
                }
            }
        }
        catch (URISyntaxException syntaxException) {
            throw new ServerCreationException(syntaxException.getMessage());
        }
        ServerSocket serverSocket;
        int port=SERVER_PORT;
        if (args.length!=0 && args[0]!=null) {
            try {
                port=Integer.parseInt(args[0]);
                if (port<49152 || port>65535) {
                    throw new ServerCreationException("Out of range");
                }
            }
            catch (NumberFormatException nfe) {
                throw new ServerCreationException("Not a valid number");
            }
        }
        try {
            serverSocket=new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        serveClient(clientSocket);
                    } catch (NullPointerException npe) {
                        System.out.println("Client disconnected abruptly");
                        npe.printStackTrace();
                    }
                }).start();
            }
        }
        catch (IOException io) {
            throw new ServerCreationException("Port busy");
        }
    }

    private static void serveClient(Socket clientSocket) {
        boolean clientSatisfied=false;
        String username=null;
        Message inMessage;
        try {
            ObjectInputStream in=new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out=new ObjectOutputStream(clientSocket.getOutputStream());
            inMessage=(Message)in.readObject();
            username=inMessage.getSender();
            System.out.println("New client: "+username);
            out.writeObject(MessageCenter.genMessage(MsgType.CONNECT,"SERVER","Welcome message","Connected to server with username: "+username));
            while (!clientSatisfied) {
                checkLobbies();
                out.writeObject(MessageCenter.genMessage(MsgType.INPUT,"SERVER","C > Create-J > Join-D > Disconnect",username+", create or join lobby?\nOtherwise disconnect"));
                clientSatisfied=processRequest(username,clientSocket,in,out);
            }
            System.out.println("Client ["+username+"]'s request processed");
        }
        catch (IOException io) {
            System.out.println(username+" disconnected abruptly");
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Could not determine what client has sent");
        }
    }

    private static boolean processRequest(String username, Socket clientSocket, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        String lobbyName;
        try {
            Info_Message response=(Info_Message)in.readObject();
            switch (response.getInfo().toUpperCase()) {
                case "C":
                case "CREATE":
                    out.writeObject(MessageCenter.genMessage(MsgType.INPUT, "SERVER", "Lobby id", "Enter lobby id: "));
                    response=(Info_Message)in.readObject();
                    lobbyName=response.getInfo();
                    synchronized (lobbies) {
                        if (lobbies.get(lobbyName) == null) {
                            out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Creating lobby","Lobby ["+lobbyName+"] created, entering now"));
                            LobbyClient lobbyMaster=new LobbyClient(username, lobbyName, clientSocket, in, out);
                            Lobby newLobby=new Lobby(lobbyName,username,lobbyMaster);
                            lobbies.put(lobbyName,newLobby);
                            new Thread(lobbies.get(lobbyName)).start();
                            return true;
                        } else {
                            out.writeObject(MessageCenter.genMessage(MsgType.ERROR,"SERVER","Error: lobby already exists","Lobby already exists, try joining instead"));
                            return false;
                        }
                    }
                case "J":
                case "JOIN":
                    synchronized (lobbies) {
                        if (lobbies.size()==0) {
                            out.writeObject(MessageCenter.genMessage(MsgType.ERROR,"SERVER","No lobbies","There are no lobbies to join, try creating one instead"));
                            return false;
                        }
                        StringBuilder lobbyStats=new StringBuilder();
                        for (String lobby : lobbies.keySet()) {
                            lobbyStats.append(lobbies.get(lobby).getLobbyStats()).append("-");
                        }
                        out.writeObject(MessageCenter.genMessage(MsgType.INPUT,"SERVER", lobbyStats.substring(0,lobbyStats.length()-1),"Select a lobby:"));
                        response=(Info_Message) in.readObject();
                        lobbyName=response.getInfo();
                        if (lobbies.get(lobbyName) == null) {
                            out.writeObject(MessageCenter.genMessage(MsgType.TEXT, "SERVER", "Error: lobby does not exist", "Lobby not found"));
                            return false;
                        } else {
                            return joinLobby(lobbies.get(lobbyName), lobbyName, username, clientSocket, in, out);
                        }
                    }
                case "D":
                case "DISCONNECT":
                    out.writeObject(MessageCenter.genMessage(MsgType.DISCONNECT, "SERVER", "Disconnecting", "Goodbye"));
                    clientSocket.close();
                    return true;
                default:
                    out.writeObject(MessageCenter.genMessage(MsgType.TEXT, "SERVER", "FORMAT ERROR", "Invalid format"));
                    return false;
            }
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Error receiving message");
            return false;
        }
        catch (NullPointerException npe) {
            System.out.println(username+" abruptly disconnected");
            return false;
        }
    }

    private static boolean joinLobby(Lobby lobby, String lobbyName, String username,Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        int maxPlayers=lobby.getMaxPlayers();
        try {
            if (maxPlayers==0) {
                out.writeObject(MessageCenter.genMessage(MsgType.ERROR,"SERVER","Lobby not ready","Lobby not yet ready, try joining later"));
                System.out.println(username+" tried to join lobby "+lobbyName+" but it was not ready");
            } else {
                synchronized (lobby.getClients()) {
                    if (lobby.getClients().size()<maxPlayers) {
                        if (lobby.getClients().get(username)==null) {
                            if (!(lobby.getWhiteList().size()==0 || lobby.getWhiteList().contains(username))) {
                                out.writeObject(MessageCenter.genMessage(MsgType.ERROR,"SERVER","Not on whitelist","You are not on the whitelist: "+lobby.getWhiteList()));
                                return false;
                            }
                            lobby.connectClient(new LobbyClient(username,lobbyName,socket,in,out));
                            return true;
                        } else {
                            out.writeObject(MessageCenter.genMessage(MsgType.ERROR,"SERVER","Username already in use","Player with that username already exists"));
                            System.out.println(username+" is a duplicate username for lobby "+lobbyName);
                        }
                    } else {
                        out.writeObject(MessageCenter.genMessage(MsgType.ERROR,"SERVER","Lobby full","Lobby is full ["+maxPlayers+"/"+maxPlayers+"]"));
                        System.out.println(username+" tried to join lobby "+lobbyName+", but it was full");
                    }
                }
            }
        }
        catch (IOException io) {
            System.out.println("Error sending message to client during lobby join");
        }
        return false;
    }

    private static void checkLobbies() {
        for (String lobbyName : lobbies.keySet()) {
            Lobby lobby=lobbies.get(lobbyName);
            if (lobby.getClients().size()==0) {
                System.out.println("Closed lobby "+lobbyName);
                lobbies.remove(lobbyName);
            } else {
                System.out.println("Checked lobby "+lobbyName+" "+lobby.getClients().size()+"/"+lobby.getMaxPlayers());
            }
        }
    }
}
