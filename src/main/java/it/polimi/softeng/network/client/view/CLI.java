package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.Properties;

public class CLI implements View, Runnable {

    private String username=null;
    private final BufferedReader in;
    private ObjectOutputStream toServer;
    private ReducedGame model;
    private Properties properties=new Properties();

    public CLI() {
        this.in=new BufferedReader(new InputStreamReader(System.in));
        try {
            this.properties.load(getClass().getResourceAsStream("/Assets/CLI/CLI.properties"));
        }
        catch (IOException io) {
            System.out.println("Could not find properties file, defaulting to basic CLI");
            this.properties=null;
        }
    }

    @Override
    public void main() {
        try {
            String line;
            BufferedReader print=new BufferedReader(new FileReader(properties.getProperty("Logo")));
            while ((line=print.readLine())!=null) {
                System.out.println(line);
            }
        }
        catch (IOException io) {
            System.out.println("Eriantys");
        }

    }

    @Override
    public void run() {
        Message outMessage=null;
        while (outMessage==null || outMessage.getSubType()!= MsgType.DISCONNECT) {
            try {
                outMessage=parseMessage(in.readLine().split(" "));
                if (outMessage!=null) {
                    sendMessage(outMessage);
                }
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
    public void setModel(ReducedGame model) {
        this.model=model;
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
        this.username=username;
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
        Colour diskColour=Colour.parseChosenColour(input[0]);
        if (diskColour!=null) {
            if (input.length<2) {
                display("Error, not enough arguments");
                return null;
            }
            if (input[1].equalsIgnoreCase("DINING")) {
                return MessageCenter.genMessage(MsgType.DISKTODININGROOM,username,"Dining room",diskColour);
            } else {
                return MessageCenter.genMessage(MsgType.DISKTOISLAND,username,input[1],diskColour);
            }
        }
        switch (input[0].toUpperCase()) {
            case "CHAR":
            case "CHARACTER":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                String options="";
                for (int i=2; i<input.length; i++) {
                    options=options.concat(input[i]).concat(" ");
                }
                return MessageCenter.genMessage(MsgType.PLAYCHARCARD,username,input[1],options);
            case "ASSIST":
            case "ASSISTANT":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                return MessageCenter.genMessage(MsgType.PLAYASSISTCARD,username,input[1],null);
            case "REFILL":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                return MessageCenter.genMessage(MsgType.CHOOSECLOUD,username,input[1],null);
            case "MSG":
            case "WHISPER":
                if (input.length<3) {
                    display("Not enough arguments");
                    return null;
                }
                String message="";
                for (int i=2; i<input.length; i++) {
                    message=message.concat(input[i]);
                }
                return MessageCenter.genMessage(MsgType.WHISPER,username,input[1],message);
            case "":
                display("For list of commands type help");
                return null;
            case "HELP":
                display("Possible commands:\n" +
                        "- [Colour] dining - moves student disk to dining room\n" +
                        "- [Colour] [island id] - moves student disk to island\n" +
                        "- char | character [char id] - play character card\n" +
                        "- assist | assistant [assist id] - play assistant card\n" +
                        "- refill [cloud id] - choose cloud to refill entrance from\n" +
                        "- msg | whisper [username] - send a message to another player");
                return null;
            default:
                return MessageCenter.genMessage(MsgType.TEXT,username,"Basic response",input[0]);
        }
    }
}
