package it.polimi.softeng.network.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])$";
    //TODO: Add message type class
    private static final ArrayList<String> errorMessages=new ArrayList<>(Arrays.asList("Disconnected","SERVER FULL","SERVER CLOSED"));
    public static void main(String[] args) throws IOException {
        String inputLine;
        Socket socket=null;
        BufferedReader userInput=new BufferedReader(new InputStreamReader(System.in));
        while (socket==null) {
            socket=connectToServer(userInput);
        }
        PrintWriter toServer=new PrintWriter(socket.getOutputStream(),true);
        BufferedReader fromServer=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while((inputLine=fromServer.readLine())!=null && !errorMessages.contains(inputLine)) {
            System.out.println(inputLine);
            toServer.println(userInput.readLine());
        }
        System.out.println("DISCONNECTED");
        System.out.println(inputLine);
        userInput.close();
        toServer.close();
        fromServer.close();
        socket.close();
    }

        public static Socket connectToServer(BufferedReader in) {
            Pattern pattern=Pattern.compile(IP_FORMAT);
            Matcher matcher;
            String inputLine,serverIP=null;
            Integer port=null;
            try {
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
                return null;
            }
        }
        catch (IOException io) {
            System.out.println("Error connecting to server");
            return null;
        }
    }
}
