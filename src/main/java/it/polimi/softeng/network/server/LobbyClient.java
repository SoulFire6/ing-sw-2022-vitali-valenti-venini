package it.polimi.softeng.network.server;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.*;
import java.net.Socket;

public class LobbyClient {
    private final String username;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public LobbyClient(String username, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
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
    public Message getMessage() {
        try {
            return (Message)this.in.readObject();
        }
        catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }
    public void sendMessage(MsgType type, String lobbyName, String context, Object msg) {
        try {
            this.out.writeObject(MessageCenter.genMessage(type,null,lobbyName,context,msg));
            this.out.flush();
            this.out.reset();
        }
        catch (IOException io) {
            System.out.println("Error sending message to "+username);
        }
    }
    public void sendMessage(Message message) {
        try {
            this.out.writeObject(message);
            this.out.flush();
            this.out.reset();
        }
        catch (IOException io) {
            System.out.println("Error sending message to "+username);
        }
    }
}
