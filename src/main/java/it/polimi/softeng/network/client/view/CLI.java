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

public class CLI implements View {
    private String username=null;
    private final BufferedReader in;
    private ObjectOutputStream toServer=null;

    private boolean loadedGame=false;
    private final Properties properties=new Properties();
    private final Properties characterInfo=new Properties();

    public CLI() {
        this.in=new BufferedReader(new InputStreamReader(System.in));
        try {
            this.properties.load(getClass().getResourceAsStream("/Assets/CLI/CLI.properties"));
            this.characterInfo.load(getClass().getResourceAsStream("/CardData/CharacterCards.properties"));
        }
        catch (IOException io) {
            switchDisplayColour(Colour.RED.name(),false);
            display("Could not find properties file, defaulting to basic CLI");
            resetDisplayStile();
        }
    }
    @Override
    public void run() {
        try {
            switchDisplayColour(Colour.RED.name(),false);
            switchDisplayColour(Colour.BLUE.name(),true);
            String line;
            BufferedReader print=new BufferedReader(new FileReader(properties.getProperty("LOGO")));
            while ((line=print.readLine())!=null) {
                display(line);
            }
            resetDisplayStile();

            print=new BufferedReader(new FileReader(properties.getProperty("SCHOOLBOARD")));
            while((line=print.readLine())!=null) {
                display(line);
            }
        }
        catch (IOException io) {
            display("Eriantys");
        }
        while (toServer==null) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException ignored) {
            }
        }
        while (!loadedGame) {

        }
        Message outMessage=null;
        while (outMessage==null || outMessage.getSubType()!=MsgType.CLOSE) {
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
    }

    @Override
    public void setToServer(ObjectOutputStream toServer) {
        this.toServer=toServer;
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
            display("Username: ");
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

    public void switchDisplayColour(String colour, boolean background) {
        if (background) {
            System.out.print((char)27+properties.getProperty(("ANSI_BG_"+colour.toUpperCase())));
        } else {
            System.out.print((char)27+properties.getProperty(("ANSI_"+colour.toUpperCase())));
        }
    }
    public void resetDisplayStile() {
        System.out.print((char)27+properties.getProperty("ANSI_RESET"));
        System.out.print((char)27+properties.getProperty("ANSI_BG_RESET"));
    }

    public void modelSync(ReducedGame model) {
        loadedGame=true;
        new Thread(()->{
            synchronized (model) {
                while(true) {
                    printModel(model);
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void printModel(ReducedGame model) {
        //TODO implement print game
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
                    message=message.concat(input[i]+" ");
                }
                return MessageCenter.genMessage(MsgType.WHISPER,username,input[1],message);
            case "CHARINFO":
                if (input.length<2) {
                    display("Not enough arguments");
                    return null;
                }
                String info;
                boolean found=false;
                System.out.println(input[1]+"_setup");
                System.out.println(characterInfo.getProperty("monk_setup"));
                if ((info=characterInfo.getProperty(input[1].toLowerCase()+"_setup"))!=null) {
                    display("Setup: "+info);
                    found=true;
                }
                if ((info=characterInfo.getProperty(input[1].toLowerCase()+"_effect"))!=null) {
                    display("Effect:"+info);
                    found=true;
                }
                if (!found) {
                    System.out.println("Character id not found");
                }
                return null;
            case "HELP":
                display("Possible commands:\n" +
                        "- [Colour] dining - moves student disk to dining room\n" +
                        "- [Colour] [island id] - moves student disk to island\n" +
                        "- char | character [char id] - play character card\n" +
                        "- assist | assistant [assist id] - play assistant card\n" +
                        "- refill [cloud id] - choose cloud to refill entrance from\n" +
                        "- msg | whisper [username] - send a message to another player\n" +
                        "- charinfo [char name] - prints character card info");
                return null;
            default:
                display("For list of commands type help");
                return null;
                //return MessageCenter.genMessage(MsgType.TEXT,username,"Basic response",input[0]);
        }
    }
}
