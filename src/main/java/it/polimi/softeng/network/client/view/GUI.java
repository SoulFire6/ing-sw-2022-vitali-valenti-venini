package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class GUI implements View, Runnable {

    private ObjectOutputStream toServer;

    public GUI() {

    }

    @Override
    public void run() {
        //TODO: runs once model is loaded from server, update view every time model changes
    }

    @Override
    public void setToServer(ObjectOutputStream toServer) {
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
        return null;
    }

    @Override
    public String setIP(String defaultIP) {
        return null;
    }

    @Override
    public int setPort(int defaultPort) {
        return 0;
    }

    @Override
    public void display(String message) {

    }
}
