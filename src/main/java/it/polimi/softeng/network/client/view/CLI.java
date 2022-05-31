package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.model.Team;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
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
            display(getDisplayColour(Colour.RED.name(),false));
            display("Could not find properties file, defaulting to basic CLI");
            resetDisplayStile();
        }
    }
    @Override
    public void run() {
        try {
            display(getDisplayColour(Colour.RED.name(),false));
            String line;
            BufferedReader print=new BufferedReader(new FileReader(properties.getProperty("LOGO")));
            while ((line=print.readLine())!=null) {
                display(line);
            }
            resetDisplayStile();
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

    public String getDisplayColour(String colour, boolean background) {
        if (background) {
            return (char)27+properties.getProperty(("ANSI_BG_"+colour.toUpperCase()));
        } else {
            return (char)27+properties.getProperty(("ANSI_"+colour.toUpperCase()));
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
                        model.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void printModel(ReducedGame model) {
        String dash="▬",wall="▌";
        ReducedPlayer firstPlayer,secondPlayer;
        int schoolBoardLength=36;
        String schoolBoardDelimiter=String.format("%-"+schoolBoardLength+"s",dash.repeat(schoolBoardLength));
        clearScreen();
        StringBuilder modelUI=new StringBuilder();
        for (int i=0; i<2; i++) {
            firstPlayer=model.getPlayers().size()>i*2?model.getPlayers().get(i*2):null;
            secondPlayer=model.getPlayers().size()>i*2+1?model.getPlayers().get(i*2+1):null;
            modelUI.append(String.format("%-"+schoolBoardLength+"s",firstPlayer!=null? model.getPlayers().get(i*2).getName()+"'s SchoolBoard":"")).append("  ");
            modelUI.append(String.format("%-"+schoolBoardLength+"s",secondPlayer!=null?model.getPlayers().get(i*2+1).getName()+"'s SchoolBoard":""));
            modelUI.append(firstPlayer!=null?"\n":"");
            modelUI.append(String.format("%-"+schoolBoardLength+"s",firstPlayer!=null?("Team: "+firstPlayer.getTeam()+(model.isExpertMode()?"  Coins: "+firstPlayer.getSchoolBoard().getCoins():"")):""));
            modelUI.append("  ").append(String.format("%-"+schoolBoardLength+"s",secondPlayer!=null?("Team: "+secondPlayer.getTeam()+(model.isExpertMode()?"  Coins: "+secondPlayer.getSchoolBoard().getCoins():"")):"")).append("\n");
            modelUI.append(String.format("%-"+schoolBoardLength+"s",firstPlayer!=null?dash.repeat(schoolBoardLength):"")).append("  ").append(String.format("%-"+schoolBoardLength+"s",secondPlayer!=null?dash.repeat(schoolBoardLength-1)+" ":" ")).append(firstPlayer!=null?"\n":"");
            for (Colour c : Colour.values()) {
                if (firstPlayer!=null) {
                    modelUI.append(getSchoolBoardRow(c,firstPlayer.getTeam(),firstPlayer.getSchoolBoard()));
                }
                if (secondPlayer!=null) {
                    modelUI.append(getSchoolBoardRow(c,secondPlayer.getTeam(),secondPlayer.getSchoolBoard()));
                } else {
                    modelUI.append(" ".repeat(schoolBoardLength));
                }
                if (firstPlayer!=null) {
                    modelUI.append("\n");
                }
            }
            if (firstPlayer!=null) {
                modelUI.append(schoolBoardDelimiter).append("  ").append(secondPlayer!=null?schoolBoardDelimiter:" ").append("\n");
            }
        }
        modelUI.append("\n");
        for (int i=0; i<model.getIslands().size(); i++) {
            modelUI.append(getIslandStats(model.getIslands().get(i)));
            modelUI.append((i+1)%4==0?"\n":" | ");
        }
        for (ReducedCloud cloud : model.getClouds()) {
            modelUI.append(getTileStats(cloud.getId(),cloud.getContents())).append(" | ");
        }
        modelUI.append(getTileStats(model.getBag().getId(),model.getBag().getContents())).append("\n");
        ArrayList<ReducedAssistantCard> hand=null;
        for (ReducedPlayer player : model.getPlayers()) {
            if (player.getName().equals(username)) {
                hand=player.getSchoolBoard().getHand();
                break;
            }
        }
        if (model.isExpertMode()) {
            for (int i=0; i<model.getCharacterCards().size(); i++) {
                modelUI.append(getCharacterData(model.getCharacterCards().get(i)));
                modelUI.append(i== model.getCharacterCards().size()-1?"\n":" | ");
            }
        }
        if (hand!=null) {
            for (int i=0; i<6; i++) {
                switch (i) {
                    case 0:
                    case 5:
                        modelUI.append((" "+dash.repeat(8)+" ").repeat(hand.size()));
                        break;
                    case 1:
                        for (ReducedAssistantCard card : hand) {
                            modelUI.append(wall).append(String.format("%-8s",card.getId())).append(wall);
                        }
                        break;
                    case 4:
                        for (ReducedAssistantCard card : hand) {
                            modelUI.append(wall).append(" ").append(String.format("%-3d", card.getTurnValue())).append(String.format("%3d", card.getMotherNatureValue())).append(" ").append(wall);
                        }
                        break;
                    default:
                        modelUI.append((wall+" ".repeat(8)+wall).repeat(hand.size()));
                        break;
                }
                modelUI.append("\n");
            }
        } else {
            modelUI.append("ERROR LOADING HAND");
        }
        System.out.println(modelUI);
    }

    private String getSchoolBoardRow(Colour c, Team t, ReducedSchoolBoard board) {
        String wall="▌";
        String fontColour=getDisplayColour(c.name(),false);
        String resetColour=getDisplayColour ("RESET",false);
        return wall+(fontColour+"♦ "+board.getEntrance().get(c)+" "+resetColour+wall+fontColour+" ♦".repeat(board.getDiningRoom().get(c))+resetColour+" ♦".repeat(10-board.getDiningRoom().get(c))+" "+wall+(board.getProfessorTable().get(c)?fontColour+"◘"+resetColour+" ":"◘ ")+wall+(board.getTowers()>=((2*c.ordinal()+1))?"▲ ":"  ")+((board.getTowers()>=(c.ordinal()+1)*2)?"▲ ":"  "))+wall+"  ";
    }

    private String getIslandStats(ReducedIsland island) {
        StringBuilder islandStats=new StringBuilder();
        islandStats.append(island.getID()).append(": ");
        for (Colour c : Colour.values()) {
            islandStats.append(getDisplayColour(c.name(),false)).append(island.getContents().get(c)).append(" ");
        }
        islandStats.append(getDisplayColour("RESET",false));
        if (island.getTeam()!=null) {
            islandStats.append(getDisplayColour(island.getTeam().name(),false)).append(getDisplayColour(Colour.BLUE.name(),true)).append(" ▲ ").append(getDisplayColour("RESET",false)).append(getDisplayColour("RESET",true));
        }
        return islandStats.toString();
    }

    private String getTileStats(String id, EnumMap<Colour,Integer> contents) {
        StringBuilder tileStats=new StringBuilder();
        tileStats.append(id).append(": ");
        for (Colour c : Colour.values()) {
            tileStats.append(getDisplayColour(c.name(),false)).append(contents.get(c)).append(" ");
        }
        tileStats.append(getDisplayColour("RESET",false));
        return tileStats.toString();
    }

    private String getCharacterData(ReducedCharacterCard card) {
        return card.getId();
        //TODO add memory data
    }

    private void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (IOException io) {
            System.out.println("Could not clear screen");
        }
    }

    public Message parseMessage(String[] input) {
        if (!loadedGame) {
            return MessageCenter.genMessage(MsgType.TEXT,username,"Basic response",input[0]);
        }
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
                return MessageCenter.genMessage(MsgType.PLAYASSISTCARD,username,input[1],input[1]);
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
        }
    }
}
