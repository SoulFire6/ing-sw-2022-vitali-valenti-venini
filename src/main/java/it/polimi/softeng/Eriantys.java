package it.polimi.softeng;

import it.polimi.softeng.network.client.Client;
import it.polimi.softeng.network.server.Server;

import java.io.IOException;

public class Eriantys {
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private final static String[] helpMessage= {
            "Usage: java Eriantys [-client -u username -ip <server ip> -port <server-port> [-cli | -gui] ]",
            "                     [-server -port <server port>]",
            "\nArguments:",
            "-client | -c : run client",
            "-server | -s: run server",
            "-user | -u : specifies username",
            "-cli : client-only, use command line input",
            "-gui : client-only, use graphical user interface",
            "-ip : client-only, specifies server ip for client, if not found will default to "+DEFAULT_IP,
            "-port | -p : specify server port, if not found will default to " + DEFAULT_PORT};

    public static void main(String[] args) throws IllegalArgumentException{
        //username,ip,port,cli/gui
        String[] clientArgs={null,null,null,null};
        //port
        String[] serverArgs={null};
        boolean isClient = false, isServer = false, stop=false;
        if (args.length > 0) {
            for (int i = 0; i<args.length && !stop; i++) {
                switch (args[i].toLowerCase()) {
                    case "--help":
                        for (String s : helpMessage) {
                            System.out.println(s);
                        }
                        stop=true;
                        break;
                    case "-client":
                    case "-c":
                        if (isServer) {
                            throw new IllegalArgumentException("Already server: cannot be client");
                        }
                        isClient = true;
                        break;
                    case "-server":
                    case "-s":
                        if (isClient) {
                            throw new IllegalArgumentException("Already client: cannot be server");
                        }
                        isServer=true;
                        break;
                    case "-user":
                    case "-u":
                        if (clientArgs[0]!=null) {
                            throw new IllegalArgumentException("Already specified username");
                        }
                        if (i+1>args.length) {
                            throw new IllegalArgumentException("Missing username");
                        }
                        if (args[i+1].startsWith("-")) {
                            throw new IllegalArgumentException("Invalid username");
                        }
                        clientArgs[0]=args[++i];
                        break;
                    case "-cli":
                        if (clientArgs[3]!=null) {
                            throw new IllegalArgumentException("Cannot be both cli and gui");
                        }
                        clientArgs[3]="CLI";
                        break;
                    case "-gui":
                        if (clientArgs[3]!=null) {
                            throw new IllegalArgumentException("Cannot be both cli and gui");
                        }
                        clientArgs[3]="GUI";
                        break;
                    case "-ip":
                        if (clientArgs[1]!=null) {
                            throw new IllegalArgumentException("Already set ip");
                        }
                        clientArgs[1]=args[++i];
                        serverArgs[0]=clientArgs[1];
                        break;
                    case "-port":
                    case "-p":
                        if (clientArgs[2]!=null) {
                            throw new IllegalArgumentException("Already set port");
                        }
                        clientArgs[2]=args[++i];
                        serverArgs[0]=clientArgs[2];
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid parameter");
                }
            }
            if (!stop) {
                try {
                    if (isClient && clientArgs[3]!=null) {
                        Client client=new Client(clientArgs);
                        client.start();
                    } else if (isServer) {
                        Server.main(serverArgs);
                    } else {
                        throw new IllegalArgumentException("Did not specify client or server");
                    }
                }
                catch (IOException io) {
                    System.out.println(io.getMessage());
                }
            }
        } else {
            System.out.println("Please specify arguments, use --help for more info");
        }
    }
}