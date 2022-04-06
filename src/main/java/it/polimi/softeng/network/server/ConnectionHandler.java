package it.polimi.softeng.network.server;

//import it.polimi.softeng.network.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandler implements Runnable{
    private final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;
    //TODO: add message class
    //private ConcurrentLinkedQueue<Message> messageQueue=new ConcurrentLinkedQueue<>();
    private static final ArrayList<String> errorMessages=new ArrayList<>(Arrays.asList("SERVER FULL","SERVER CLOSED",null));

    public ConnectionHandler(Socket clientSocket) throws IOException {
        this.clientSocket=clientSocket;
        this.in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out=new PrintWriter(clientSocket.getOutputStream(),true);
    }
    public ConnectionHandler(Socket clientSocket, BufferedReader in, PrintWriter out) {
        this.clientSocket=clientSocket;
        this.in=in;
        this.out=out;
    }
    @Override
    public void run(){
        try {
            out.println("CONNECTED");
            String nextLine;
            while (!errorMessages.contains(nextLine = in.readLine())) {
                System.out.println(nextLine);
                out.println(nextLine);
            }
            out.println("Disconnected");
            in.close();
            out.close();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
    }
    public Socket getSocket() {
        return this.clientSocket;
    }
}
