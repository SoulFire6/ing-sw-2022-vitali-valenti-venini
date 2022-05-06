package it.polimi.softeng.network.client;

import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.client.view.CLI;
import it.polimi.softeng.network.client.view.GUI;
import it.polimi.softeng.network.client.view.View;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client {
    private String username;
    private ReducedGame model;
    private View view;

    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$";

    private Socket socket=null;
    private ObjectOutputStream toServer=null;
    private ObjectInputStream fromServer=null;

    public Client(String[] args) {
        this.model=null;
        this.view=(args[3].equals("GUI")?new GUI():new CLI());
        connectToServer(args);
    }
    //TODO: remove, this is for testing purposes only (so it's easier to start clients)
    public static void main(String[] args) {
        String[] testArgs={null,null,null,"cli"};
        Client client=new Client(testArgs);
        client.start();
    }
    public void start() {
        if (socket == null) {
            System.out.println("Error: did not initialise client first");
            return;
        }
        Message inMessage = null;
        try {
            toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT, username, null, null));
            while ((inMessage = (Message) fromServer.readObject()) != null && inMessage.getSubType() != MsgType.DISCONNECT) {
                parseMessageFromServer(inMessage);
                //TODO make view send out messages
            }
            toServer.close();
            fromServer.close();
            socket.close();
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Error reading message from server");
        } catch (IOException io) {
            System.out.println("IO exception");
        }
        if (inMessage != null && inMessage.getSubType() == MsgType.DISCONNECT) {
            System.out.println("DISCONNECTED");
        } else {
            System.out.println("Error: abrupt disconnect");
        }
    }

    private void connectToServer(String[] args) {
        String serverIP;
        int serverPort=0;
        Pattern pattern=Pattern.compile(IP_FORMAT);
        Matcher matcher;
        this.username=args[0]!=null?args[0]:view.setUsername();
        while (this.socket==null) {
            try {
                if (args[1]!=null && pattern.matcher(args[1]).matches()) {
                    serverIP=args[1];
                } else {
                    do {
                        serverIP=view.setIP(DEFAULT_IP);
                        matcher=pattern.matcher(serverIP);
                    }while(!matcher.matches());
                }
                if (args[2]!=null) {
                    try {
                        serverPort=Integer.parseInt(args[2]);
                    }
                    catch (NumberFormatException nfe) {
                        view.display("Provided value for port \"+args[2]+\" is not a number");
                    }
                }
                while(serverPort<49152 || serverPort>65535) {
                    view.display("Server port (default 50033, range 49152-65535): ");
                    serverPort=view.setPort(DEFAULT_PORT);
                }
                this.socket=new Socket(serverIP,serverPort);
            }
            catch (IOException io) {
                System.out.println("Error connecting to server");
            }
        }
        try {
            this.toServer=new ObjectOutputStream(socket.getOutputStream());
            this.fromServer=new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException io) {
            System.out.println("IO exception occurred");
            //retrying
            connectToServer(args);
        }
    }
    //TODO implement
    public void parseMessageFromServer(Message message) {

    }
    //TODO finish cli string parsing
    public static Message parseInput(String input) {
        return null;
    }
    //TODO add gui event parsing
    public static Message parseInput() {
        return null;
    }
}
