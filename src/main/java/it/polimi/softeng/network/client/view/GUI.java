package it.polimi.softeng.network.client.view;

public class GUI implements View, Runnable {
    @Override
    public void run() {
        //TODO: runs once model is loaded from server, update view every time model changes
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
