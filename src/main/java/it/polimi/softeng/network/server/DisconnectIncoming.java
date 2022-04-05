package it.polimi.softeng.network.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DisconnectIncoming implements Runnable{
    private final ServerSocket serverSocket;
    public DisconnectIncoming(ServerSocket socket) {
        this.serverSocket=socket;
    }

    @Override
    public void run() {
        Socket clientSocket;
        try {
            while((clientSocket=serverSocket.accept())!=null) {
                PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
                out.println("SERVER FULL");
                out.close();
                clientSocket.close();
            }
        }
        catch (IOException io) {
            io.printStackTrace();
        }
    }
}
