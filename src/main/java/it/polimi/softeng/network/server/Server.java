package it.polimi.softeng.network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private static final HashMap<String,Lobby> lobbies=new HashMap<>();
    private static final Integer SERVER_PORT=50033;
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(SERVER_PORT);
        Socket clientSocket;
        while (true) {
            try {
                clientSocket=serverSocket.accept();
                serveClient(clientSocket);
            } catch (NullPointerException npe) {
                System.out.println("Client disconnected abruptly");
            }
        }
    }

    private static void serveClient(Socket clientSocket) {
        boolean clientSatisfied=false;
        String username=null;
        try {
            BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
            out.println("CONNECTED\nEnter your name: \nDONE");
            username=in.readLine();
            out.println("Username: "+username+"\n[create] or [join] lobby?\nDONE");
            System.out.println("New client: "+username);
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
    }

    private static boolean processRequest(String username, Socket clientSocket, BufferedReader in, PrintWriter out) throws IOException {
        boolean status=false;
        String inputLine=in.readLine();
        switch (inputLine) {
            case "create":
                out.println("Enter lobby id: \nDONE");
                inputLine=in.readLine();
                if (lobbies.get(inputLine)==null) {
                    out.println("Created lobby "+inputLine+", entering now...");
                    lobbies.put(inputLine,new Lobby(inputLine,username, new LobbyClient(username,clientSocket,in,out)));
                    new Thread(lobbies.get(inputLine)).start();
                    status=true;
                } else {
                    out.println("Lobby already exists, try joining instead\nDONE");
                }
                break;
            case "join":
                out.println("Enter lobby id: \nDONE");
                inputLine=in.readLine();
                if (lobbies.get(inputLine)==null) {
                    out.println("Lobby not found\nDONE");
                } else {
                    joinLobby(lobbies.get(inputLine),username,clientSocket,in,out);
                }
                break;
            case "disconnect":
                out.println("Disconnecting\nDONE");
                clientSocket.close();
                status=true;
                break;
            default:
                out.println("Invalid format\nDONE");
                break;
        }
        return status;
    }

    private static void joinLobby(Lobby lobby, String username, Socket socket, BufferedReader in, PrintWriter out) {
        int maxPlayers=lobby.getMaxPlayers();
        if (maxPlayers==0) {
            out.println("Lobby not yet setup");
        } else {
            synchronized (lobby.getClients()) {
                HashMap<String,LobbyClient> clients=lobby.getClients();
                if (clients.size()<maxPlayers) {
                    if (clients.get(username)==null) {
                        clients.put(username,new LobbyClient(username,socket,in,out));
                        clients.notify();
                    } else {
                        out.println("Player with that username already exists");
                    }
                } else {
                    out.println("Max clients reached for lobby");
                }
            }
        }
    }
}
