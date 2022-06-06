package it.polimi.softeng.network.server;

import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private static final HashMap<String,Lobby> lobbies=new HashMap<>();
    private static final Integer SERVER_PORT=50033;
    public static void main(String[] args) {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        ServerSocket serverSocket;
        Integer port=SERVER_PORT;
        if (args.length!=0 && args[0]!=null) {
            try {
                port=Integer.parseInt(args[0]);
                if (port<49152 || port>65535) {
                    System.out.println("Out of range");
                }
            }
            catch (NumberFormatException nfe) {
                System.out.println("Not a valid number");
                port=null;
            }
            String portNumber;
            while (port==null || port<49152 || port>65535) {
                System.out.print("Server port (default 50033, range 49152-65535): ");
                try {
                    if ((portNumber=in.readLine()).length()!=0 && !portNumber.equals("local")) {
                        port=Integer.parseInt(portNumber);
                    } else {
                        port=SERVER_PORT;
                    }
                }
                catch (IOException io) {
                    System.out.println("Error reading port number");
                }
                catch (NumberFormatException nfe) {
                    System.out.println("Not a valid number");
                }
            }
        }
        try {
            serverSocket=new ServerSocket(port);
            Socket clientSocket;
            while (true) {
                try {
                    clientSocket=serverSocket.accept();
                    serveClient(clientSocket);
                } catch (NullPointerException npe) {
                    System.out.println("Client disconnected abruptly");
                    break;
                }
            }
        }
        catch (IOException io) {
            System.out.println("Port busy");
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
            out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Welcome message","Connected to server with username: "+username));
            while (!clientSatisfied) {
                checkLobbies();
                out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Serving client",username+", [create] or [join] lobby?\nOtherwise [disconnect]"));
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
        boolean status=false;
        String lobbyName;
        try {
            Info_Message response=(Info_Message)in.readObject();
            switch (response.getInfo().toUpperCase()) {
                case "C":
                case "CREATE":
                    for (String lobby : lobbies.keySet()) {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Listing lobbies",lobbies.get(lobby).getLobbyStats()));
                    }
                    out.writeObject(MessageCenter.genMessage(MsgType.TEXT, "SERVER", "Lobby id", "Enter lobby id: "));
                    response=(Info_Message)in.readObject();
                    lobbyName=response.getInfo();
                    if (lobbies.get(lobbyName) == null) {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Creating lobby","Lobby ["+lobbyName+"] created, entering now"));
                        LobbyClient lobbyMaster=new LobbyClient(username, lobbyName, clientSocket, in, out);
                        Lobby newLobby=new Lobby(lobbyName,username,lobbyMaster);
                        lobbies.put(lobbyName,newLobby);
                        new Thread(lobbies.get(lobbyName)).start();
                        status=true;
                    } else {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Error: lobby already exists","Lobby already exists, try joining instead"));
                    }
                    break;
                case "J":
                case "JOIN":
                    if (lobbies.size()==0) {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","No lobbies","There are no lobbies to join, try creating one instead"));
                        break;
                    }
                    for (String lobby : lobbies.keySet()) {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Listing lobbies",lobbies.get(lobby).getLobbyStats()));
                    }
                    out.writeObject(MessageCenter.genMessage(MsgType.TEXT, "SERVER", "Lobby id", "Enter lobby id: "));
                    response=(Info_Message) in.readObject();
                    lobbyName=response.getInfo();
                    if (lobbies.get(lobbyName) == null) {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT, "SERVER", "Error: lobby does not exist", "Lobby not found"));
                    } else {
                        status=joinLobby(lobbies.get(lobbyName), lobbyName, username, clientSocket, in, out);
                    }
                    break;
                case "D":
                case "DISCONNECT":
                    out.writeObject(MessageCenter.genMessage(MsgType.DISCONNECT, "SERVER", "Disconnecting", "Goodbye"));
                    clientSocket.close();
                    status = true;
                    break;
                default:
                    out.writeObject(MessageCenter.genMessage(MsgType.TEXT, "SERVER", "FORMAT ERROR", "Invalid format"));
                    break;
            }
            return status;
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Error receiving message");
            return false;
        }
    }

    private static boolean joinLobby(Lobby lobby, String lobbyName, String username,Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        int maxPlayers=lobby.getMaxPlayers();
        boolean status=false;
        try {
            if (maxPlayers==0) {
                out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Lobby not ready","Lobby not yet ready, try joining later"));
                System.out.println(username+" tried to join lobby "+lobbyName+" but it was not ready");
            } else {
                synchronized (lobby.getClients()) {
                    if (lobby.getClients().size()<maxPlayers) {
                        if (lobby.getClients().get(username)==null) {
                            lobby.getClients().put(username,new LobbyClient(username,lobbyName,socket,in,out));
                            lobby.getClients().notify();
                            status=true;
                        } else {
                            out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Username already in use","Player with that username already exists"));
                            System.out.println(username+" is a duplicate username for lobby "+lobbyName);
                        }
                    } else {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,"SERVER","Lobby full","Lobby is full ["+maxPlayers+"/"+maxPlayers+"]"));
                        System.out.println(username+" tried to join lobby "+lobbyName+", but it was full");
                    }
                }
            }
        }
        catch (IOException io) {
            System.out.println("Error sending message to client during lobby join");
        }
        return status;
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
