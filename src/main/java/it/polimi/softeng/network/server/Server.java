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
    public static void main(String[] args) throws IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        ServerSocket serverSocket;
        Integer port=SERVER_PORT;
        if (args.length!=0) {
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
            out.writeObject(MessageCenter.genMessage(MsgType.TEXT,null,"SERVER","Welcome message","Connected to server with username: "+username));
            out.writeObject(MessageCenter.genMessage(MsgType.INPUT,null,"SERVER","Serving client",username+", [create] or [join] lobby?\nOtherwise [disconnect]"));
            while (!clientSatisfied) {
                clientSatisfied=processRequest(username,clientSocket,in,out);
            }
            System.out.println("Client ["+username+"]'s request processed");
        }
        catch (IOException io) {
            System.out.println("IO EXCEPTION");
        }
        catch (NullPointerException npe) {
            if (username==null) {
                System.out.println("Client disconnected abruptly");
            } else {
                System.out.println(username+" disconnected abruptly");
            }
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
                case "CREATE":
                    out.writeObject(MessageCenter.genMessage(MsgType.INPUT, null, "SERVER", "Lobby id", "Enter lobby id: "));
                    response=(Info_Message)in.readObject();
                    lobbyName=response.getInfo();
                    if (lobbies.get(lobbyName) == null) {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,null,"SERVER","Creating lobby","Lobby ["+lobbyName+"] created, entering now"));
                        lobbies.put(lobbyName, new Lobby(lobbyName, username, new LobbyClient(username, lobbyName, clientSocket, in, out)));
                        new Thread(lobbies.get(lobbyName)).start();
                        status=true;
                    } else {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,null,"SERVER","Error: lobby already exists","Lobby already exists, try joining instead"));
                    }
                    break;
                case "JOIN":
                    out.writeObject(MessageCenter.genMessage(MsgType.INPUT, null, "SERVER", "Lobby id", "Enter lobby id: "));
                    response=(Info_Message) in.readObject();
                    lobbyName=response.getInfo();
                    if (lobbies.get(lobbyName) == null) {
                        out.writeObject(MessageCenter.genMessage(MsgType.INPUT, null, "SERVER", "Error: lobby does not exist", "Lobby not found"));
                    } else {
                        joinLobby(lobbies.get(lobbyName), lobbyName, username, clientSocket, in, out);
                        status=true;
                    }
                    break;
                case "DISCONNECT":
                    out.writeObject(MessageCenter.genMessage(MsgType.DISCONNECT, null, "SERVER", "Disconnecting", "Goodbye"));
                    clientSocket.close();
                    status = true;
                    break;
                default:
                    out.writeObject(MessageCenter.genMessage(MsgType.INPUT, null, "SERVER", "FORMAT ERROR", "Invalid format"));
                    break;
            }
            return status;
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Error receiving message");
            return false;
        }
    }

    private static void joinLobby(Lobby lobby, String username, String lobbyName,Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        int maxPlayers=lobby.getMaxPlayers();
        try {
            if (maxPlayers==0) {
                out.writeObject(MessageCenter.genMessage(MsgType.TEXT,null,"SERVER","Lobby not ready","Lobby not yet ready, try joining later"));

            } else {
                synchronized (lobby.getClients()) {
                    HashMap<String,LobbyClient> clients=lobby.getClients();
                    if (clients.size()<maxPlayers) {
                        if (clients.get(username)==null) {
                            clients.put(username,new LobbyClient(username,lobbyName,socket,in,out));
                            clients.notify();
                        } else {
                            out.writeObject(MessageCenter.genMessage(MsgType.TEXT,null,"SERVER","Username already in use","Player with that username already exists"));
                        }
                    } else {
                        out.writeObject(MessageCenter.genMessage(MsgType.TEXT,null,"SERVER","Lobby full","Lobby is full ["+maxPlayers+"/"+maxPlayers+"]"));
                    }
                }
            }
        }
        catch (IOException io) {
            System.out.println("Error sending message to client during lobby join");
        }
    }
}
