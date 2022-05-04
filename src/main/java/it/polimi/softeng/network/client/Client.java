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
    private static String username=null;
    //TODO: add client controller attribute that processes messages
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])$";
    public static void main(String[] args) throws IOException{
        Message inMessage=null;
        Socket socket;
        BufferedReader userInput=new BufferedReader(new InputStreamReader(System.in));
        socket=connectToServer(args,userInput);
        if (socket==null) {
            throw new IOException("Error connecting to server");
        }
        try {
            ObjectOutputStream toServer=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromServer=new ObjectInputStream(socket.getInputStream());
            //sends username to server for identification
            toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT,username,null,null));
            while ((inMessage=(Message)fromServer.readObject())!=null && inMessage.getSubType()!=MsgType.DISCONNECT) {
                parseMessage(inMessage);
                if (inMessage.getSubType()==MsgType.INPUT) {
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
        if (inMessage!=null && inMessage.getSubType()==MsgType.DISCONNECT) {
            System.out.println("DISCONNECTED");
        } else {
            System.out.println("Error: abrupt disconnect");
        }

    }

    private static Socket connectToServer(String[] args, BufferedReader in) {
        Socket socket=null;
        while (username==null) {
            if (args.length==0) {
                System.out.print("Username: ");
                try {
                    username=in.readLine();
                }
                catch (IOException io) {
                    System.out.println("Error reading username");
                }
            } else {
                setUsername(args[0],in);
            }
        }
        try {
            switch (args.length) {
                case 4:
                    //TODO setup cli/gui based on 4th value
                    break;
                case 3:
                    socket=new Socket(setIP(args[1],in),setPort(args[2],in));
                    break;
                default:
                    socket=new Socket(setIP(null,in),setPort(null,in));
                    break;
            }

            if (socket!=null && socket.isConnected()) {
                return socket;
            } else {
                throw new IOException();
            }
        }
        catch (IOException io) {
            System.out.println("Error connecting to server");
            return null;
        }
    }
    private static void setUsername(String name, BufferedReader in) {
        while (username==null) {
            System.out.print("Choose a username: ");
            try {
                username=in.readLine();
            }
            catch (IOException io) {
                System.out.println("Error reading input for username");
            }
        }
    }
    private static String setIP(String ip, BufferedReader in) {
        Pattern pattern=Pattern.compile(IP_FORMAT);
        Matcher matcher;
        if (ip!=null && !pattern.matcher(ip).matches()) {
            ip=null;
        }
        while (ip==null) {
            System.out.print("Server ip (default local): ");
            try {
                if ((ip = in.readLine()).length()!=0 && !ip.equals("local")) {
                    matcher=pattern.matcher(ip);
                    if (!matcher.matches()) {
                        System.out.println("Wrong format for ip address");
                        ip=null;
                    }
                } else {
                    ip=DEFAULT_IP;
                }
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }
        }
        return ip;
    }
    private static int setPort(String portNumber, BufferedReader in) {
        Integer port;
        try {
            port=Integer.parseInt(portNumber);
        }
        catch (NumberFormatException nfe) {
            port=null;
        }
        while (port==null || port<49152 || port>65535) {
            try {
                System.out.print("Server port (default 50033, range 49152-65535): ");
                if ((portNumber=in.readLine()).length()!=0 && !portNumber.equals("local")) {
                    port=Integer.parseInt(portNumber);
                } else {
                    port=DEFAULT_PORT;
                }
            }
            catch (IOException io) {
                System.out.println("Error reading input");
            }
            catch (NumberFormatException nfe) {
                System.out.println("Not a valid number");
            }
        }
        return port;
    }
    //TODO: move message methods as NON STATIC methods into client controller
    private static void parseMessage(Message msg) {
        switch (msg.getType()) {
            case INFO:
                System.out.println("["+msg.getSender()+"]: "+((Info_Message)msg).getInfo());
                break;
            case LOAD:
                System.out.println(msg.getType()+" "+((Load_Message)msg).getLoad());
                break;
        }
    }
    private static Message outMessage(String msg) {
        //TODO: add more message types
        return MessageCenter.genMessage(MsgType.TEXT,username,"",msg);
    }
}
