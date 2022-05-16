package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

public class CLI implements View, Runnable {

    private final BufferedReader in;
    private ObjectOutputStream toServer;

    public CLI() {
        this.in=new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void main() {
        System.out.println("Welcome to Eriantys");
    }

    @Override
    public void run() {
        String input;
        Message outMessage=null;
        while (outMessage==null || outMessage.getSubType()!= MsgType.DISCONNECT) {
            try {
                outMessage=parseMessage(in.readLine().split(" "));
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }

        }
        display("Not yet implemented");
        //TODO: runs once model is loaded from server, update view every time model changes
        //TODO: add loop for sending messages
    }

    @Override
    public void setToServer(ObjectOutputStream toServer) {
        this.toServer=toServer;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            toServer.writeObject(message);
            toServer.flush();
            toServer.reset();
        }
        catch (IOException io) {
            display("Could not send message");
        }
    }


    @Override
    public String setUsername() {
        String username=null;
        while (username==null) {
            System.out.print("Username: ");
            try {
                username=in.readLine();
            }
            catch (IOException io) {
                display("\nError reading input");
            }
        }
        return username;
    }

    @Override
    public String setIP(String defaultIP) {
        String ip;
        try {
            System.out.print("Server ip (local or empty for default localhost): ");
            ip=in.readLine();
            if (ip.equals("") || ip.equals("local")) {
                return defaultIP;
            }
            return ip;
        }
        catch (IOException io) {
            display("Error reading input");
            return null;
        }
    }

    @Override
    public int setPort(int defaultPort) {
        String port=null;
        try {
            System.out.print("Server port (local or empty for default 50033): ");
            port=in.readLine();
            if (port.equals("") || port.equals("local")) {
                return defaultPort;
            }
            return Integer.parseInt(port);
        }
        catch (IOException io) {
            display("Error reading input");
            return -1;
        }
        catch (NumberFormatException nfe) {
            display(port+" is not a number");
            return -1;
        }
    }

    @Override
    public void display(String message) {
        System.out.println(message);
    }

    public Message parseMessage(String[] input) {
        return null;
    }
}
