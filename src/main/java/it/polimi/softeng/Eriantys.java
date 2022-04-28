package it.polimi.softeng;

import it.polimi.softeng.network.client.Client;
import it.polimi.softeng.network.server.Server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Eriantys {
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-9])$";
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


    //TODO: turn back to void main and add throw exceptions for missingArgument, invalidArgument
    public static int main(String[] args) {
        //username,ip,port
        String[] clientArgs={null,null,null};
        //port
        String[] serverArgs={null};
        boolean client = false, server = false, cli = false, gui = false;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i].toLowerCase()) {
                    case "--help":
                        for (String s : helpMessage) {
                            System.out.println(s);
                        }
                        return 0;
                    case "-client":
                    case "-c":
                        if (server) {
                            System.out.println("Already server: cannot be client and server");
                            return -1;
                        }
                        client = true;
                        break;
                    case "server":
                    case "-s":
                        if (client) {
                            System.out.println("Already client: cannot be both client and server");
                            return -1;
                        }
                        server=true;
                    case "-user":
                    case "-u":
                        if (clientArgs[0]!=null) {
                            System.out.println("Already specified username");
                        }
                        if (i+1>args.length) {
                            System.out.println("Missing/Invalid username");
                            return -1;
                        }
                        if (args[i+1].contains("-")) {
                            //TODO: throw invalid argument
                            System.out.println("Invalid argument");
                            return -1;
                        }
                        clientArgs[0]=args[++i];
                        break;
                    case "-cli":
                        if (gui) {
                            System.out.println("Cannot be gui and cli");
                            return -1;
                        }
                        cli=true;
                        break;
                    case "-gui":
                        if (cli) {
                            System.out.println("Cannot be gui and cli");
                            return -1;
                        }
                        gui=true;
                        break;
                    case "-ip":
                        if (clientArgs[1]!=null) {
                            System.out.println("Already set ip to "+clientArgs[1]);
                            return -1;
                        }
                        clientArgs[1]=args[++i];
                        serverArgs[0]=clientArgs[1];
                        break;
                    case "-port":
                    case "-p":
                        if (clientArgs[2]!=null) {
                            System.out.println("Already set port to "+clientArgs[2]);
                            return -1;
                        }
                        clientArgs[2]=args[++i];
                        serverArgs[0]=clientArgs[2];
                        break;
                    default:
                        System.out.println("Invalid argument");
                        return -1;

                }
            }
        } else {
            System.out.println("Please specify arguments, use --help for more info");
            return 0;
        }

        if (client) {
            if (clientArgs[0]==null) {
                System.out.println("Missing username");
            }
            if (cli) {
                //TODO: remove username, ip and port from client setup as they are passed by argument
                //Client.main(clientArgs);
            } else if (gui) {
                System.out.println("Not yet implemented");
                return -1;
            } else {
                System.out.println("Did not specify cli or gui, MISSING ARGUMENT");
            }

        } else if (server) {
            //Server.main(serverArgs);
        } else {
            System.out.println("Missing run type, MISSING ARGUMENT");
            return -1;
        }
        return 0;
    }
}