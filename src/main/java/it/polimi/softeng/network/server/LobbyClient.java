package it.polimi.softeng.network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LobbyClient {
    private final String username;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public LobbyClient(String username, Socket socket, BufferedReader in, PrintWriter out) {
        this.username=username;
        this.socket=socket;
        this.in=in;
        this.out=out;
    }
    public String getUsername() {
        return this.username;
    }
    public Socket getSocket() {
        return this.socket;
    }
    public String getIn() throws IOException {
        return this.in.readLine();
    }
    public void printOut(String msg) {
        this.out.println(msg);
    }
}
