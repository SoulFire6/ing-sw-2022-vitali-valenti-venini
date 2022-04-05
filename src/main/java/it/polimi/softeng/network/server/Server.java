package it.polimi.softeng.network.server;

import it.polimi.softeng.model.Colour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static ArrayList<ConnectionHandler> clients = new ArrayList<>();
    private static final int PORT=50033;

    public static void main(String[] args) throws IOException {
        int maxClients=0;
        ServerSocket serverSocket=new ServerSocket(PORT);
        Socket firstClient=serverSocket.accept();
        BufferedReader in=new BufferedReader(new InputStreamReader(firstClient.getInputStream()));
        PrintWriter out=new PrintWriter(firstClient.getOutputStream(),true);
        out.println("Player num? (2-4): ");
        while(maxClients<2 || maxClients>4) {
            System.out.println("ENTER");
            try {
                maxClients=Integer.parseInt(in.readLine());
                switch (maxClients) {
                    case 2:
                        out.println("2");
                    case 3:
                        out.println("3");
                    case 4:
                        out.println("4");
                    default:
                        break;
                }
            }
            catch (NumberFormatException nfe) {
                if (firstClient.getInputStream().read()==-1) {
                    System.exit(-1);
                }
                out.println("Incorrect format");
            }
        }
        System.out.println("GOT MAX CLIENTS "+maxClients);
        clients.add(new ConnectionHandler(firstClient,in,out));
        while (clients.size()<maxClients) {
            Socket clientSocket = serverSocket.accept();
            ConnectionHandler clientThread=new ConnectionHandler(clientSocket);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
        System.out.println("REACHED MAX CLIENTS");
        new Thread(new DisconnectIncoming(serverSocket)).start();
        System.out.println("NEW CLIENTS WILL BE DISCONNECTED");
        while (clients.size()>0) {
            for (ConnectionHandler client: clients) {
                if (!client.getSocket().isConnected()) {
                    clients.remove(client);
                    System.out.println("REMOVED DISCONNECTED CLIENT");
                }
            }
        }
        System.out.println("CLOSING");
        for (ConnectionHandler client: clients) {
            client.getSocket().close();
        }
    }

    //TEST METHOD TO BE MOVED TO CONTROLLER
    public static String respondWith(String in) {
        String[] splitInput=in.toUpperCase().split(" ");
        Colour chosenColour;
        if ((chosenColour=Colour.parseChosenColour(splitInput[0]))!=null) {
            if (splitInput.length==2) {
                return "MOVE Student to "+splitInput[1];
            } else {
                return "ERROR, no movement for "+chosenColour;
            }
        } else {
            switch (splitInput[0]) {
                case "MN":
                    return "MOVE MOTHER NATURE";
                case "TXT":
                    if (splitInput.length>2) {
                        return printColour(splitInput[1])+splitInput[2]+printColour("reset");
                    } else {
                        return printColour("red")+"ERROR"+printColour("reset");
                    }
                default:
                    return "ERROR: "+splitInput[0]+" not recognised";

            }
        }
    }
    public static String printColour(String str) {
        try {
            switch (Enum.valueOf(Colour.class,str.toUpperCase())) {
                case RED:
                    return (char)27+"[31m";
                case BLUE:
                    return (char)27+"[34m";
                case GREEN:
                    return (char)27+"[32m";
                case YELLOW:
                    return (char)27+"[33m";
                case PURPLE:
                    return (char)27+"[35m";
                default:
                    return (char)27+"[0m";
            }
        }
        catch (Exception e) {
            return (char)27+"[0m";
        }

    }
}
