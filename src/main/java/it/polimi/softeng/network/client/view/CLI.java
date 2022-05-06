package it.polimi.softeng.network.client.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLI implements View, Runnable {

    private final BufferedReader in;

    public CLI() {
        this.in=new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        System.out.println("Not yet implemented");
        //TODO: runs once model is loaded from server, update view every time model changes
        //TODO: add loop for sending messages
    }
    @Override
    public String setUsername() {
        String username=null;
        while (username==null) {
            try {
                System.out.println("Username: ");
                username=in.readLine();
            }
            catch (IOException io) {
                System.out.println("\nError reading input");
            }
        }
        return username;
    }

    @Override
    public String setIP(String defaultIP) {
        String ip;
        try {
            System.out.println("Server ip (format: [0-255].[0-255].[0-255].[0-255], local or empty for default localhost): ");
            ip=in.readLine();
            if (ip.equals("") || ip.equals("local")) {
                return defaultIP;
            }
            return ip;
        }
        catch (IOException io) {
            System.out.println("Error reading input");
            return null;
        }
    }

    @Override
    public int setPort(int defaultPort) {
        String port=null;
        try {
            port=in.readLine();
            if (port.equals("") || port.equals("local")) {
                return defaultPort;
            }
            return Integer.parseInt(port);
        }
        catch (IOException io) {
            System.out.println("Error reading input");
            return -1;
        }
        catch (NumberFormatException nfe) {
            System.out.println(port+" is not a number");
            return -1;
        }
    }

    @Override
    public void display(String message) {
        System.out.println(message);
    }
}
