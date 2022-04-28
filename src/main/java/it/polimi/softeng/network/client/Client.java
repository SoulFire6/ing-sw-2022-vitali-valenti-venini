package it.polimi.softeng.network.client;

import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import it.polimi.softeng.network.message.load.Load_Message;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static String username;
    //TODO: add client controller attribute that processes messages
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])$";
    public static void main(String[] args) {
        Message inMessage=null;
        Socket socket=null;
        BufferedReader userInput=new BufferedReader(new InputStreamReader(System.in));
        while (socket==null) {
            socket=connectToServer(userInput);
        }
        try {
            ObjectOutputStream toServer=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromServer=new ObjectInputStream(socket.getInputStream());
            //sends username to server for identification
            toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT,null,username,null,null));
            while ((inMessage=(Message)fromServer.readObject())!=null && inMessage.getType()!=MsgType.DISCONNECT) {
                parseMessage(inMessage);
                if (inMessage.getType()==MsgType.INPUT) {
                    toServer.writeObject(outMessage(userInput.readLine()));
                }
            }
            userInput.close();
            toServer.close();
            fromServer.close();
            socket.close();
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Error reading message from server");
        }
        catch (IOException io) {
            System.out.println("IO exception");
        }
        if (inMessage.getType()==MsgType.DISCONNECT) {
            System.out.println("DISCONNECTED");
        } else {
            System.out.println("Error: abrupt disconnect");
        }

    }

    private static Socket connectToServer(BufferedReader in) {
        Pattern pattern=Pattern.compile(IP_FORMAT);
        Matcher matcher;
        String inputLine,serverIP=null;
        Integer port=null;
        System.out.println("Choose a username: ");
        try {
            username=in.readLine();
            while (serverIP==null) {
                System.out.print("Server ip (default local): ");
                if ((inputLine = in.readLine()).length()!=0 && !inputLine.equals("local")) {
                    matcher=pattern.matcher(inputLine);
                    if (matcher.matches()) {
                        System.out.println("SERVER IP CORRECT");
                        serverIP=inputLine;
                    } else {
                        System.out.println("Wrong format for ip address");
                    }
                } else {
                    serverIP=DEFAULT_IP;
                }
            }
            while (port==null || port<49152 || port>65535) {
                System.out.print("Server port (default 50033, range 49152-65535): ");
                if ((inputLine = in.readLine()).length()!=0 && !inputLine.equals("local")) {
                    try {
                        port = Integer.valueOf(inputLine);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Not a valid number");
                    }
                } else {
                    port=DEFAULT_PORT;
                }
            }
        }
        catch (IOException io) {
            System.out.println("Error reading input");
            return null;
        }
        try {
            Socket socket=new Socket(serverIP,port);
            if (socket.isConnected()) {
                return socket;
            } else {
                System.out.println("Server error");
                return null;
            }
        }
        catch (IOException io) {
            System.out.println("Error connecting to server");
            return null;
        }
    }
    //TODO: move message methods as NON STATIC methods into client controller
    private static void parseMessage(Message msg) {
        switch (msg.getType()) {
            case TEXT:
            case INPUT:
            case DISCONNECT:
                System.out.println("["+msg.getSender()+"]: "+((Info_Message)msg).getInfo());
                break;
            case LOAD:
                System.out.println(((Load_Message) msg).getLoadType()+" "+((Load_Message)msg).getLoadType());
                break;
        }
    }
    private static Message outMessage(String msg) {
        //TODO: add more message types
        return MessageCenter.genMessage(MsgType.TEXT,null,username,"",msg);
    }
}
