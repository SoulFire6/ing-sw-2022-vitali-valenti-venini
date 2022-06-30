package it.polimi.softeng.network.server;

import it.polimi.softeng.exceptions.LobbyClientDisconnectedException;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.*;
import java.net.Socket;

/**
 * This class represent the Client and its interaction with the lobby.
 * It defines the methods necessary for the Client to send and receive messages over the lobby.
 */
public class LobbyClient {
    private final String username;
    private final String lobbyName;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    /**
     * Constructor of the class LobbyClient
     * @param username String name of the client
     * @param lobbyName String name of the lobby
     * @param socket Socket between this client and the server
     * @param in objectInputStream to receive messages
     * @param out objectOutputStream to send messages
     */
    public LobbyClient(String username, String lobbyName,Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.username=username;
        this.lobbyName=lobbyName;
        this.socket=socket;
        this.in=in;
        this.out=out;
    }

    /**
     * @return String username of the client
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @return Socket between this client and the server
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * @return Message from the ObjectInputStream
     */
    public Message getMessage() {
        try {
            return (Message)this.in.readObject();
        }
        catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }

    /**
     * @param type MsgType of the message to send
     * @param context String explanation of the message
     * @param msg Object representing the message content
     * @throws LobbyClientDisconnectedException if the client does disconnect from the lobby
     * @see MsgType
     */
    public void sendMessage(MsgType type, String context, Object msg) throws LobbyClientDisconnectedException {
        try {
            this.out.writeObject(MessageCenter.genMessage(type,lobbyName,context,msg));
            this.out.flush();
            this.out.reset();
        }
        catch (IOException io) {
            throw new LobbyClientDisconnectedException(username);
        }
    }

    /**
     * @param message the Message to send through the ObjectOutputStream
     * @throws LobbyClientDisconnectedException if the client does disconnect from the lobby
     */
    public void sendMessage(Message message) throws LobbyClientDisconnectedException {
        try {
            this.out.writeObject(message);
            this.out.flush();
            this.out.reset();
        }
        catch (IOException io) {
            throw new LobbyClientDisconnectedException(username);
        }
    }
}
